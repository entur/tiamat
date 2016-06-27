# !/bin/bash

# Download gtfs zip files exported from chouette (s32)
# Extract stops.txt from all zip files and append into stops.txt
# Generate stop place data with tiamat and bootstrap profile

destinationFile="stops.txt"
folder="gtfs-"$(date +%Y-%m-%d-%H:%M:%S)

rm $destinationFile
touch $destinationFile
mkdir $folder
echo "Created folder $folder"

aws s3 cp s3://rutebanken-marduk/outbound/gtfs/ $folder --recursive


for file in $folder/*zip
do
  echo "Appending stops.txt from $file to $destinationFile"
  unzip -p $file stops.txt >> $destinationFile
done

lines=$(wc -l $destinationFile)
echo "$lines"

echo "Removing duplicates and sorting"
sort $destinationFile | uniq > $destinationFile-tmp
mv $destinationFile-tmp $destinationFile

lines=$(wc -l $destinationFile)

echo "$lines"
