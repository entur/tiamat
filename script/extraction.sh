# !/bin/bash

# Download gtfs zip files exported from chouette (s32)
# Extract stops.txt from all zip files and append into stops.txt
# Generate stop place data with tiamat and bootstrap profile

destinationFile="stops.txt"
folder="gtfs-"$(date +%Y-%m-%d-%H:%M:%S)

stopsHeader="stop_id,stop_code,stop_name,stop_desc,stop_lat,stop_lon,zone_id,stop_url,location_type,parent_station,wheelchair_boarding,stop_timezone"

rm $destinationFile
touch $destinationFile
mkdir $folder
echo "Created folder $folder"

aws s3 cp s3://rutebanken-marduk/outbound/gtfs/ $folder --recursive

for file in $folder/*zip
do
  echo "Appending stops.txt from $file to $destinationFile"
  unzip -p $file stops.txt | grep -v $stopsHeader >> $destinationFile
done

lines=$(wc -l $destinationFile)
echo "$lines"

echo "Removing duplicates and sorting"
sort $destinationFile | uniq > $destinationFile-tmp
mv $destinationFile-tmp $destinationFile

sed -i "1s/^/$stopsHeader\n/" $destinationFile

lines=$(wc -l $destinationFile)

echo "$lines"


destinationDir="../src/main/resources/stops/"
echo "Copying $destinationFile into $destinationDir"
cp $destinationFile $destinationDir

cd ..

mvn clean install spring-boot:run -Dspring.config.location=src/test/resources/application.properties -DskipTests -Dspring.profiles.active=geodb,bootstrap -Dserver.port=1999
