#!/bin/bash

#service apache2 stop
service tomcat6 stop

echo
ps ax | grep java
echo

echo
echo "Creating database backup..."
su postgres -c pg_dump $DATABASE > delivery_backup.sql
echo "Finished database backup."
echo 




echo
curl -u fakeuser:fakepass --cookie-jar cookies.txt --insecure 'https://bamboo.socialhistoryservices.org/userlogin!default.action?os_authType=basic' --head
curl --cookie cookies.txt --insecure https://bamboo.socialhistoryservices.org/browse/DELIVERY-CORETEST/latestSuccessful/artifact/JOB1/1.0/Delivery.war > Delivery.war
echo


echo
echo "Removing old deployment files"
rm -rf /var/lib/tomcat6/webapps/ROOT
rm /var/log/tomcat6/*
echo
mv Delivery.war /var/lib/tomcat6/webapps/ROOT.war
ls -al /var/lib/tomcat6/webapps

echo

service tomcat6 start

echo
if [ ! -d "delivery_shop" ]; then
    echo "Cloning delivery_shop repository"
    git clone git://github.com/IISH/delivery_shop.git delivery_shop
    echo "Please modify config params of delivery_shop before you continue."
    read -p "Press [Enter] key when config params are set correctly."
fi
cp -r delivery_shop /var/lib/tomcat6/webapps/ROOT/resources/


echo "Now restart the apache service with: service apache2 stop, service apache2 start"
