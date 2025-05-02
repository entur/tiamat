/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.service;

import com.google.cloud.storage.Storage;
import org.rutebanken.helper.gcp.BlobStoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.InputStream;


@Service
@Profile("gcs-blobstore")
public class GcsBlobStoreService implements BlobStoreService {

    private static final Logger logger = LoggerFactory.getLogger(GcsBlobStoreService.class);

    private final String bucketName;

    private final String blobPath;
    private final String projectId;


    public GcsBlobStoreService(@Value("${blobstore.gcs.bucket.name}") String bucketName,
                               @Value("${blobstore.gcs.blob.path}") String blobPath,
                               @Value("${blobstore.gcs.project.id}") String projectId) {

        this.bucketName = bucketName;
        this.blobPath = blobPath;
        this.projectId = projectId;
    }

    public void upload(String fileName, InputStream inputStream) {
        Storage storage = getStorage();
        String blobIdName = createBlobIdName(blobPath, fileName);
        try {
            logger.info("Uploading {} to path {} in bucket {}", fileName, blobPath, bucketName);
            BlobStoreHelper.createOrReplace(storage, bucketName, blobIdName, inputStream, false);
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file " + fileName + ", blobIdName " + blobIdName + " to bucket " + bucketName, e);
        }
    }

    private Storage getStorage() {
        try {
            logger.info("Get storage for project {}", projectId);
                return BlobStoreHelper.getStorage(projectId);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error setting up BlobStore from blobstore.gcs.project.id '" + projectId + "'", e);
        }
    }

    public InputStream download(String fileName) {
        String blobIdName = createBlobIdName(blobPath, fileName);
        return BlobStoreHelper.getBlob(getStorage(), bucketName, blobIdName);
    }

    public String createBlobIdName(String blobPath, String fileName) {
        return blobPath + '/' + fileName;
    }
}
