# !/bin/bash

# Download gtfs zip files exported from chouette (s32)
# Extract stops.txt from all zip files and append into stops.txt
# Generate stop place data with tiamat and bootstrap profile

stopsFile="stops.txt"
agencyFile="agency.txt"
folder="gtfs-"$(date +%Y-%m-%d-%H:%M:%S)

stopsHeader="stop_id,stop_code,stop_name,stop_desc,stop_lat,stop_lon,zone_id,stop_url,location_type,parent_station,wheelchair_boarding,stop_timezone"
agencyHeader="agency_id,agency_name,agency_url,agency_timezone,agency_phone"

rm $stopsFile
rm $agencyFile
touch $stopsFile
touch $agencyFile

mkdir $folder
echo "Created folder $folder"

aws s3 cp s3://rutebanken-marduk/outbound/gtfs/ $folder --recursive

for file in $folder/*zip
do
  echo "Appending stops.txt from $file to $stopsFile"
  unzip -p $file stops.txt | grep -v $stopsHeader >> $stopsFile
  unzip -p $file agency.txt | grep -v $agencyHeader >> $agencyFile
done

lines=$(wc -l $stopsFile)
echo "$lines"

echo "Removing duplicates and sorting stops"
sort -u -t, -k1,1 $stopsFile > $stopsFile-tmp
mv $stopsFile-tmp $stopsFile

echo "Add header to $stopsFile"
sed -i "1s/^/$stopsHeader\n/" $stopsFile

echo "Removing duplicates and sorting agencies"
sort -u -t, -k1,1 $agencyFile > $agencyFile-tmp
mv $agencyFile-tmp $agencyFile

echo "Add header to $agencyFile"
sed -i "1s/^/$agencyHeader\n/" $agencyFile


lines=$(wc -l $stopsFile)

echo "$lines"

destinationDir="../src/main/resources/stops/"
echo "Copying $stopsFile into $destinationDir"
cp $stopsFile $destinationDir

echo "Copying $agencyFile into $destinationDir"
cp $agencyFile $destinationDir


cd ..

mvn clean install spring-boot:run -Dspring.config.location=src/test/resources/application.properties -DskipTests -Dspring.profiles.active=geodb,bootstrap -Dserver.port=1999
