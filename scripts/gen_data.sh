tpch_data=${DEVELOP_DIR}/example/tpch_data
dbgen_path=${DEVELOP_DIR}/example/tpch-dbgen

tpcc_config=${DEVELOP_DIR}/example/tpcc_config
benchmark_tool=${DEVELOP_DIR}/example/benchmarksql

function link_data_for_pixels() {
    SCALE=${1:-"1"}
    log_info "Scale ${SCALE}"
    local data_path=${PROJECT_DIR}/Data_${SCALE}x
    local target_root="${PROJECT_DIR}/Data_pixels"

    log_info "raw data path is ${data_path}"
    mkdir -p "${target_root}"

    for f in "${data_path}"/*.csv; do
        local base=$(basename "$f" .csv)
        local subdir="${target_root}/${base}"
        local link="${subdir}/$(basename "$f")"

        # ensure subdir exists
        mkdir -p "${subdir}"

        # remove old symlink if exists
        if [ -L "${link}" ] || [ -e "${link}" ]; then
            rm -f "${link}"
        fi

        # create new symlink
        ln -s "${f}" "${link}"
        log_info "Linked ${link} -> ${f} "
    done
}

function count_csv_lines() {
    SCALE=${1:-"1"}
    local data_path="${PROJECT_DIR}/Data_${SCALE}x"

    log_info "Counting lines in CSV files under ${data_path}"

    for f in "${data_path}"/*.csv; do
        if [ -f "$f" ]; then
            local lines
            lines=$(wc -l < "$f")
            echo "$(basename "$f"): $lines"
        fi
    done
}


function generate_tpch_data() {
  SCALE=${1:-"0.1"}
  log_info "DBGEN: Scale ${SCALE}"
  [[ -d ${tpch_data} ]] || { log_fatal_exit "Failed to find dir ${tpch_data}"; }

  WORK_PATH=`pwd`
  cd ${dbgen_path}
  DBGEN=${dbgen_path}/dbgen
  [[ -x ${DBGEN} ]] || { log_fatal_exit "Failed to execute ${DBGEN}. Check build process"; }
  eval `${DBGEN} -vf -s ${SCALE}`
  check_fatal_exit "Failed to generate data"
  mv -f *.tbl ${tpch_data} # TODO:Solve the tbl permission issues

  cd ${tpch_data}
  ls *.tbl | xargs md5sum >tpch_data.md5sum
  check_warning "Failed to calculate the md5sum of TPCH DATA" # If run it repeatedly, this may fail
  cd ${WORK_PATH}
}

function clean_tpch_data() {
  rm -f ${tpch_data}/*.tbl ${tpch_data}/tpch_data.md5sum
}

function build_tpcc_tool() {
    ant -f ${benchmark_tool}/build.xml
    check_fatal_exit "Can't build tpc-c benchmark tool"
}

function get_db_config_path() {
    local db_type=$1
    
    case "$db_type" in
        mysql|MySQL|MYSQL)
            echo "${tpcc_config}/props.mysql"
            ;;
        pg|postgres|postgresql|PostgreSQL|PG)
            echo "${tpcc_config}/props.pg"
            ;;
        *)
            log_fatal "Error: Unsupported database type '$db_type'"
            log_fatal "Usage: $0 {mysql|pg}"
            exit 1
            ;;
    esac
}

function build_tpcc_db() {
    local db_type=$1
    local config_path
    
    config_path=$(get_db_config_path "$db_type")
    
    log_info "Building TPC-C database for $db_type using config: $config_path"
  
    local WORK_PATH=`pwd`
    cd ${benchmark_tool}/run
    ./runDatabaseBuild.sh $config_path
    check_fatal_exit "Building TPC-C database for $db_type failed"
    cd ${WORK_PATH}
}

function start_tpcc_test() {
    local db_type=$1
    local config_path
    
    config_path=$(get_db_config_path "$db_type")
    
    log_info "Run TPC-C benchmakr for $db_type using config: $config_path"
  
    local WORK_PATH=`pwd`
    cd ${benchmark_tool}/run
    ./runBenchmark.sh $config_path
    check_fatal_exit "Run TPC-C Benchmark for $db_type failed"
    cd ${WORK_PATH}
}

function load_pg_data() {
  local DATA_DIR=${1:-${PROJECT_DIR}/Data_1x}
  log_info "load data in ${DATA_DIR}"

  [[ -z "$PGUSER" ]] && { log_fatal "PGUSER not set"; }
  [[ -z "$PGDATABASE" ]] && { log_fatal "PGDATABASE not set"; }
  [[ -z "$PGPASSWORD" ]] && { log_fatal "PGPASSWORD not set"; }

  psql -c "\copy customer from '${DATA_DIR}/customer.csv' CSV DELIMITER ',';" &
  psql -c "\copy company from '${DATA_DIR}/company.csv' CSV DELIMITER ',';" &
  psql -c "\copy transfer from '${DATA_DIR}/transfer.csv' CSV DELIMITER ',';" &
  psql -c "\copy checking from '${DATA_DIR}/checking.csv' CSV DELIMITER ',';" &
  psql -c "\copy checkingAccount from '${DATA_DIR}/checkingAccount.csv' CSV DELIMITER ',';" &
  psql -c "\copy savingAccount from '${DATA_DIR}/savingAccount.csv' CSV DELIMITER ',';" &
  psql -c "\copy loanApps from '${DATA_DIR}/loanApps.csv' CSV DELIMITER ',';" &
  psql -c "\copy loanTrans from '${DATA_DIR}/loanTrans.csv' CSV DELIMITER ',';" &

  # 等待所有后台任务完成
  wait

  log_info "All data loading jobs finished."
}

function load_pg_index() {
  local SQL_FILE=${CONFIG_DIR}/create_index_pg.sql
  log_info "Exec ${SQL_FILE}"
  [[ -z "$PGUSER" ]] && { log_fatal "PGUSER not set"; }
  [[ -z "$PGDATABASE" ]] && { log_fatal "PGDATABASE not set"; }
  [[ -z "$PGPASSWORD" ]] && { log_fatal "PGPASSWORD not set"; }

  while IFS= read -r cmd; do
      # 当已经有N个命令在执行时，等待直到其中一个执行完毕
      # jobs -p -r
      while (( $(jobs -p -r | wc -l) >= 8 )); do
          sleep 0.1
      done
      {
        log_info "EXEC SQL: ${cmd}"
        psql -c "${cmd}"
      } &
  done < ${SQL_FILE}
  wait
}

function load_pg_seq() {
  local SQL_FILE=${CONFIG_DIR}/create_index_pg.sql
  log_info "Exec ${SQL_FILE}"
  [[ -z "$PGUSER" ]] && { log_fatal "PGUSER not set"; }
  [[ -z "$PGDATABASE" ]] && { log_fatal "PGDATABASE not set"; }
  [[ -z "$PGPASSWORD" ]] && { log_fatal "PGPASSWORD not set"; }

  psql -f ${CONFIG_DIR}/create_sequence_pg.sql
  wait
}


