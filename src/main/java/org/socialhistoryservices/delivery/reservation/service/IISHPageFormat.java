/**
 * Copyright (C) 2013 International Institute of Social History
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.socialhistoryservices.delivery.reservation.service;

import java.awt.print.PageFormat;
import java.awt.print.Paper;

/**
 * The page format used for printing by the IISH.
 */
public class IISHPageFormat extends PageFormat {

    public IISHPageFormat() {
        Paper p = new Paper();

        // A4 width = 210mm = 8.26771654 inches = 595.28 size (72 times inches).
        p.setSize(595,595);

        // 10 margin on top for barcode to correctly appear.
        // smaller area in width to make surethe characters don't fall off due to left margin of printer.
        p.setImageableArea(10, 0, 595, 585);
        setPaper(p);
        setOrientation(LANDSCAPE);

    }
}
