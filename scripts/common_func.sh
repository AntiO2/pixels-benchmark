#!/bin/bash


SOURCE_PATH=`readlink -f $BASH_SOURCE` 2>/dev/null

if [[ -z ${SOURCE_PATH} ]]; then
    # for debug
    SOURCE_PATH=`readlink -f $0`
fi

SCRIPT_DIR=`dirname ${SOURCE_PATH}`
DEVELOP_DIR=`dirname ${SCRIPT_DIR}`
PROJECT_DIR=${DEVELOP_DIR}


CONFIG_DIR=${PROJECT_DIR}/conf
SECRETS_DIR=${CONFIG_DIR}/secrets

# source ${DEVELOP_DIR}/.env
source ${SCRIPT_DIR}/build_func.sh
source ${SCRIPT_DIR}/log_func.sh
source ${SCRIPT_DIR}/docker_func.sh
source ${SCRIPT_DIR}/util_func.sh
source ${SCRIPT_DIR}/gen_data.sh