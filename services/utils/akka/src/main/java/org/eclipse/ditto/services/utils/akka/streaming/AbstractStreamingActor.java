/*
 * Copyright (c) 2017 Bosch Software Innovations GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/org/documents/epl-2.0/index.php
 *
 * Contributors:
 *    Bosch Software Innovations GmbH - initial contribution
 */
package org.eclipse.ditto.services.utils.akka.streaming;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Collections;

import org.eclipse.ditto.services.utils.akka.LogUtil;

import akka.Done;
import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.DiagnosticLoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import akka.stream.ActorMaterializer;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import scala.concurrent.duration.FiniteDuration;

/**
 * Abstract actor that responds to each command by streaming elements from a source to actors specified in the command.
 *
 * @param <C> Type of commands to start a stream.
 * @param <E> Type of elements of a stream.
 */
public abstract class AbstractStreamingActor<C, E> extends AbstractActor {

    private final DiagnosticLoggingAdapter log = LogUtil.obtain(this);
    private final ActorMaterializer materializer = ActorMaterializer.create(getContext());

    /**
     * @return Class of the commands.
     */
    protected abstract Class<C> getCommandClass();

    /**
     * Extract recipient of elements from a command.
     *
     * @param command The command to start a stream.
     * @return Reference of the actor to stream elements to.
     */
    protected abstract ActorRef getElementRecipient(final C command);

    /**
     * Extract recipient of stream status messages.
     *
     * @param command The command to start a stream.
     * @return Reference of the actor to report stream status to.
     */
    protected abstract ActorRef getStatusRecipient(final C command);

    /**
     * Extract streaming rate from a command.
     *
     * @param command The command to start a stream.
     * @return The number of elements to send per second.
     */
    protected abstract int getElementsPerSecond(final C command);

    /**
     * Starts a source of elements according to the command.
     *
     * @param command The command to start a stream.
     * @return A source of elements to stream to the recipient.
     */
    protected abstract Source<E, NotUsed> createSource(final C command);

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(getCommandClass(), this::startStreaming)
                .matchAny(message -> log.warning("Unexpected message: <{}>", message))
                .build();
    }

    private void startStreaming(final C command) {
        final ActorRef elementRecipient = getElementRecipient(command);
        final ActorRef statusRecipient = getStatusRecipient(command);
        final int elementsPerSecond = getElementsPerSecond(command);
        final FiniteDuration second = FiniteDuration.create(1, SECONDS);

        createSource(command)
                .throttle(elementsPerSecond, second, elementsPerSecond, ThrottleMode.shaping())
                .mapConcat(element -> {
                    elementRecipient.tell(element, ActorRef.noSender());
                    return Collections.emptyList();
                })
                .runWith(Sink.actorRef(statusRecipient, Done.getInstance()), materializer);

    }
}
