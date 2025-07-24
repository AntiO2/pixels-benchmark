BASH_PATH=`readlink -f $0`
SCRIPT_DIR=`dirname ${BASH_PATH}`



source ${SCRIPT_DIR}/common_func.sh

check_env