echo "LOAD -o file:////home/ubuntu/disk1/Data_pixels_10x/checking -s pixels_bench_sf10x -t checking -n 300000 -r \, -c 1" | java -jar ${PIXELS_HOME}/sbin/pixels-cli-0.2.0-SNAPSHOT-full.jar
echo "LOAD -o file:////home/ubuntu/disk1/Data_pixels_10x/checkingAccount -s pixels_bench_sf10x -t checkingaccount -n 300000 -r \, -c 1" | java -jar ${PIXELS_HOME}/sbin/pixels-cli-0.2.0-SNAPSHOT-full.jar
echo "LOAD -o file:////home/ubuntu/disk1/Data_pixels_10x/company -s pixels_bench_sf10x -t company -n 300000 -r \, -c 1" | java -jar ${PIXELS_HOME}/sbin/pixels-cli-0.2.0-SNAPSHOT-full.jar
echo "LOAD -o file:////home/ubuntu/disk1/Data_pixels_10x/customer -s pixels_bench_sf10x -t customer -n 300000 -r \, -c 1" | java -jar ${PIXELS_HOME}/sbin/pixels-cli-0.2.0-SNAPSHOT-full.jar
echo "LOAD -o file:////home/ubuntu/disk1/Data_pixels_10x/loanApps -s pixels_bench_sf10x -t loanapps -n 300000 -r \, -c 1" | java -jar ${PIXELS_HOME}/sbin/pixels-cli-0.2.0-SNAPSHOT-full.jar
echo "LOAD -o file:////home/ubuntu/disk1/Data_pixels_10x/loanTrans -s pixels_bench_sf10x -t loantrans -n 300000 -r \, -c 1" | java -jar ${PIXELS_HOME}/sbin/pixels-cli-0.2.0-SNAPSHOT-full.jar
echo "LOAD -o file:////home/ubuntu/disk1/Data_pixels_10x/savingAccount -s pixels_bench_sf10x -t savingaccount -n 300000 -r \, -c 1" | java -jar ${PIXELS_HOME}/sbin/pixels-cli-0.2.0-SNAPSHOT-full.jar
echo "LOAD -o file:////home/ubuntu/disk1/Data_pixels_10x/transfer -s pixels_bench_sf10x -t transfer -n 300000 -r \, -c 10" | java -jar ${PIXELS_HOME}/sbin/pixels-cli-0.2.0-SNAPSHOT-full.jar
