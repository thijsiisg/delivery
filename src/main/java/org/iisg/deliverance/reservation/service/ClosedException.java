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

package org.iisg.deliverance.reservation.service;

/**
 * Indicates one of the holdings specified is contained within a record which
 * is either INHERIT and a parent is CLOSED, or its own restrictionType is
 * CLOSED.
 */
public class ClosedException extends Exception {

    public ClosedException() {
        super("One of the specified holdings is contained within a record " +
                "which is either INHERIT and a parent is CLOSED, " +
                "or its own restrictionType is CLOSED.");
    }
}
