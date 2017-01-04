package org.rutebanken.tiamat.service;

import org.junit.Test;

import static org.junit.Assert.*;

public class BlobStoreServiceTest {

    @Test
    public void test() {


        BlobStoreService blobStoreService = new BlobStoreService(
                "/home/cristoffer/rutebanken-config/gcloud-storage.json",
                "tiamat-test",
                "exports",
                "carbon-1287");




        String fileContents = "test fra cristoffer "+System.currentTimeMillis();

        blobStoreService.upload("file-"+System.currentTimeMillis()+"txt", fileContents);





    }


}