package org.apache.usergrid.persistence.graph.consistency;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * Simple implementation of our timeout queue using an in memory PriorityBlockingQueue.
 *
 * This SHOULD NOT be used in a production environment.  This is for development/testing runtimes only.
 * This should not be a singleton, we can have multiple instances of
 */
@Singleton
public class LocalTimeoutQueue<T> implements TimeoutQueue<T> {

    /**
     * For in memory queueing
     */
    private final PriorityBlockingQueue<AsynchonrousEvent<T>> queue = new PriorityBlockingQueue<AsynchonrousEvent<T>>( 1000, new TimeoutEventCompatator<T>() );

    private final TimeService timeService;


    @Inject
    public LocalTimeoutQueue( final TimeService timeService ) {
        this.timeService = timeService;
    }


    @Override
    public AsynchonrousEvent<T> queue( final T event, final long timeout ) {
        final long scheduledTimeout = timeService.getCurrentTime() + timeout;
        final AsynchonrousEvent<T> queuedEvent = new SimpleAsynchonrousEvent<T>( event, scheduledTimeout );

        queue.add( queuedEvent );

        return queuedEvent;
    }


    @Override
    public Collection<AsynchonrousEvent<T>> take( final int maxSize, final long timeout ) {

        final long now = timeService.getCurrentTime();
        final long newTimeout = now+timeout;

        List<AsynchonrousEvent<T>> results = new ArrayList<AsynchonrousEvent<T>>(maxSize);

        for(int i = 0; i < maxSize; i ++){

            AsynchonrousEvent<T> queuedEvent = queue.peek();

            //nothing to do
            if(queuedEvent == null){
                break;
            }


            //not yet reached timeout
            if(queuedEvent.getTimeout() > now){
                break;
            }

            final AsynchonrousEvent<T> newEvent =  new SimpleAsynchonrousEvent<T>( queuedEvent.getEvent(), newTimeout );

            //re schedule a new event to replace this one
            queue.add(newEvent);

            //we're re-added, remove the element
            queue.poll();

            results.add( newEvent );

        }

        return results;
    }


    @Override
    public boolean remove( final AsynchonrousEvent<T> event ) {
        return queue.remove( event );
    }


    private static class TimeoutEventCompatator<T> implements Comparator<AsynchonrousEvent<T>> {


        @Override
        public int compare( final AsynchonrousEvent<T> o1, final AsynchonrousEvent<T> o2 ) {
            return Long.compare( o1.getTimeout(), o2.getTimeout() );
        }
    }
}