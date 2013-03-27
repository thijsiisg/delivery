/*
 * Copyright 2011 International Institute of Social History
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.socialhistoryservices.delivery;

import org.socialhistoryservices.delivery.api.NoSuchPidException;
import org.socialhistoryservices.delivery.api.RecordLookupService;
import org.socialhistoryservices.delivery.record.entity.ExternalHoldingInfo;
import org.socialhistoryservices.delivery.record.entity.ExternalRecordInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Mocks a lookup service for testing (so tests won't fail when the real
 * lookup service is down).
 */
public class MockRecordLookupService implements RecordLookupService {

    public String getRecordTitleByPid(String pid) throws NoSuchPidException {
        // Note: Do not depend on the exact return value when using.
        return "Open Archive";
    }

    public Map<String, String> getRecordsByTitle(String title) {
        // Note: Do not depend on the exact return value when using.
        Map<String, String> result = new HashMap<String, String>();
        result.put("12345", "Open Archive");
        return result;
    }
    /**
     * Maps a PID to metadata of a record.
     * @param pid The PID to lookup.
     * @return The metadata of the record, if found.
     * @throws NoSuchPidException Thrown when the PID is not found.
     */
    public ExternalRecordInfo getRecordMetaDataByPid(String pid) throws
            NoSuchPidException {
        ExternalRecordInfo externalInfo = new ExternalRecordInfo();
        externalInfo.setTitle("Open Archive");
        externalInfo.setMaterialType(ExternalRecordInfo.MaterialType.ARCHIVE);
        return externalInfo;
    }

    public Map<String, ExternalHoldingInfo> getHoldingMetadataByPid(String pid) throws NoSuchPidException {
        return new HashMap<String, ExternalHoldingInfo>();
    }
}
