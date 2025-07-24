paimon_flink_cdc() {
export AWS_ACCESS_KEY_ID=minioadmin
export AWS_SECRET_ACCESS_KEY=minio123456

${FLINK_HOME}/bin/flink run \
    /home/pixels/downloads/paimon-flink-action-1.2.0.jar \
    kafka_sync_table \
    --warehouse s3://pixels/paimon_jdbc \
    --database  pixels \
    --table pixels_freshness \
    --kafka_conf  connector=kafka \
    --kafka_conf  topic=postgresql.oltp_server.pixels_bench_sf1x.freshness \
    --kafka_conf  properties.bootstrap.servers=localhost:29092 \
    --kafka_conf  properties.group.id=223 \
    --kafka_conf  format=debezium-json \
    --kafka_conf  scan.startup.mode=earliest-offset \
    --kafka_conf  value.format=debezium-json \
    --catalog_conf  metastore=jdbc \
    --catalog_conf  catalog-key=jdbc \
    --catalog_conf  uri=jdbc:mysql://10.77.110.35:23306/paimon \
    --catalog_conf  jdbc.user=root \
    --catalog_conf  jdbc.password=pixels_root \
    --catalog_conf  s3.endpoint=http://10.77.110.29:9000 \
    --catalog_conf  fs.native-s3.enabled=true \
    --catalog_conf  s3.region=us-east-1 \
    --catalog_conf  s3.path-style-access=true \
    --catalog_conf  s3.access-key=minioadmin \
    --catalog_conf  s3.secret-key=minio123456 \
    --primary_keys  f_cli_id \
    --table_conf    merge-engine=deduplicate
}

