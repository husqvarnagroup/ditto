/*
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.ditto.services.connectivity.messaging.persistence.stages;

import java.util.function.Consumer;

import org.eclipse.ditto.model.connectivity.ConnectionId;
import org.eclipse.ditto.services.connectivity.messaging.monitoring.logs.ConnectionLogger;
import org.eclipse.ditto.signals.commands.connectivity.ConnectivityCommand;

/**
 * Everything needed by strategies.
 */
public final class ConnectionState {

    private final ConnectionId connectionId;
    private final ConnectionLogger connectionLogger;
    private final Consumer<ConnectivityCommand<?>> validator;

    private ConnectionState(final ConnectionId connectionId,
            final ConnectionLogger connectionLogger, final Consumer<ConnectivityCommand<?>> validator) {
        this.connectionId = connectionId;
        this.connectionLogger = connectionLogger;
        this.validator = validator;
    }

    // TODO
    public static ConnectionState of(
            final ConnectionId connectionId,
            final ConnectionLogger connectionLogger,
            final Consumer<ConnectivityCommand<?>> validator) {

        return new ConnectionState(connectionId, connectionLogger, validator);
    }

    /**
     * @return the connection ID.
     */
    public ConnectionId id() {
        return connectionId;
    }

    /**
     * @return the public logger.
     */
    public ConnectionLogger getConnectionLogger() {
        return connectionLogger;
    }

    /**
     * @return the command validator.
     */
    public Consumer<ConnectivityCommand<?>> getValidator() {
        return validator;
    }
}
