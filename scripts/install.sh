#!/bin/bash

usage() {
  cat <<EOF

EOF
  exit 0;
}


develop_debug=off
need_init=off
need_build=off

generate_data=off
data_scale=1

enable_tpch=off
enable_tpcc=off


enable_postgres=off
load_postgres=off
pg_cdc=off


enable_mysql=off
load_mysql=off

init_containers=off


for arg do
  val=`echo "$arg" | sed -e 's;^--[^=]*=;;'`

  case "$arg" in
    --develop_debug=*)          develop_debug="$val"  ;;
    --debug)                    develop_debug=on      ;;
    --init_containers)          init_containers=on    ;;
    --pg_cdc)                   pg_cdc=on             ;;
    --need_init=*)              need_init="$val"      ;;
    --enable_postgres=*)        enable_postgres="$val";;
    --enable_mysql=*)           enable_mysql="$val"   ;;
    --need_build=*)             need_build="$val"     ;;
    --generate_data=*)          generate_data="$val"  ;;
    --data_scale=*)             data_scale="$val"     ;;
    --load_postgres=*)          load_postgres="$val"  ;;
    --load_mysql=*)             load_mysql="$val"     ;;
    --enable_tpch=*)            enable_tpch="$val"    ;;
    --enable_tpcc=*)            enable_tpcc="$val"    ;;
    -h|--help)                  usage ;;
    *)                          echo "wrong options : $arg";
                                exit 1
                                ;;
  esac
done

[[ x${develop_debug} == x"on" ]] && { set -o verbose -o xtrace; }

BASH_PATH=`readlink -f $0`
SCRIPT_DIR=`dirname ${BASH_PATH}`



source ${SCRIPT_DIR}/common_func.sh

check_env


if [[ x${generate_data} == x"on" ]]; then
    echo # TODO pixels benchmark generate data
fi


if [[ x${init_containers} == x"on" ]]; then
    log_info "Init Container"
    docker compose -f ${CONFIG_DIR}/docker-compose.yml up -d
    check_fatal_exit "docker-compose up failed."
fi

if [[ x${need_init} == x"on" ]]; then
  echo
fi

log_info "Containers Started"


log_info "Start Register Debezium Connectors"

if [[ x$pg_cdc} == x"on" ]]; then
  log_info "Start Register PostgreSQL Debezium Connector"
  [[ -f ${CONFIG_DIR}/sync/pg_debezium.json ]] || { log_fatal_exit "Can't get postgres debezium connector config"; }
  wait_for_url http://localhost:8084/connectors 20
  check_fatal_exit "Postgres Source Kafka Connector Server Fail"
  # register PostgreSQL connector
  try_command curl -f -X POST -H "Content-Type: application/json" -d @${CONFIG_DIR}/register-postgres.json http://localhost:8084/connectors -w '\n'
  check_fatal_exit "Register PostgreSQL Source Connector Fail"
fi

log_info "Visit http://localhost:9000 to check kafka status"
log_info "Visit http://localhost:3000 to check Grafana Dashboard"


if [[ x${enable_tpcc} == x"on" ]]; then
  log_info "Start TPC-C Benchmark"

  if [[ x${enable_mysql} == x"on" ]]; then
    build_tpcc_db mysql
    start_tpcc_test mysql
  fi

  if [[ x${enable_postgres} == x"on" ]]; then
    build_tpcc_db pg
    start_tpcc_test pg
  fi

  log_info "End TPC-C Benchmark"
fi

