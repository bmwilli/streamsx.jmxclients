// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package streams.metric.exporter.rest.errorhandling;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import streams.metric.exporter.error.StreamsTrackerException;

@Provider
public class StreamsTrackerExceptionMapper implements
        ExceptionMapper<StreamsTrackerException> {

    private static final Logger LOGGER = LoggerFactory.getLogger("root."
            + StreamsTrackerExceptionMapper.class.getName());

    @Override
    public Response toResponse(StreamsTrackerException ex) {
        ErrorMessage errorMessage = new ErrorMessage(ex);
        setHttpStatus(ex, errorMessage);

        LOGGER.debug("RestServer StreamsMonitorExceptionManager: "
                + errorMessage);

        return Response.status(errorMessage.getStatus()).entity(errorMessage)
                .type(MediaType.APPLICATION_JSON).build();
    }

    private void setHttpStatus(StreamsTrackerException ex,
            ErrorMessage errorMessage) {

        // Interpret Internal Application Codes to HTTP Response Codes
        switch (ex.getErrorCode()) {
        case INSTANCE_NOT_FOUND: {
            errorMessage.setStatus(Response.Status.NOT_FOUND.getStatusCode());
            break;
        }
        case JOB_NOT_FOUND: {
            errorMessage.setStatus(Response.Status.NOT_FOUND.getStatusCode());
            break;
        }
        case ALL_METRICS_NOT_AVAILABLE: {
            errorMessage.setStatus(Response.Status.NOT_FOUND.getStatusCode());
            break;
        }
        case ALL_JOBS_NOT_AVAILABLE: {
            errorMessage.setStatus(Response.Status.NOT_FOUND.getStatusCode());
            break;
        }
        default: {
            errorMessage.setStatus(Response.Status.INTERNAL_SERVER_ERROR
                    .getStatusCode());

        }
        }

    }

}
