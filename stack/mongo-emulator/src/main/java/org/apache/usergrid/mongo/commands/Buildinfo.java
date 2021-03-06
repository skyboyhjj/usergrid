/*******************************************************************************
 * Copyright 2012 Apigee Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.apache.usergrid.mongo.commands;


import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.apache.usergrid.mongo.MongoChannelHandler;
import org.apache.usergrid.mongo.protocol.OpQuery;
import org.apache.usergrid.mongo.protocol.OpReply;

import static org.apache.usergrid.utils.MapUtils.entry;
import static org.apache.usergrid.utils.MapUtils.map;


public class Buildinfo extends MongoCommand {

    @Override
    public OpReply execute( MongoChannelHandler handler, ChannelHandlerContext ctx, MessageEvent e, OpQuery opQuery ) {
        OpReply reply = new OpReply( opQuery );
        reply.addDocument(
                map( entry( "version", "1.8.1" ), entry( "gitVersion", "a429cd4f535b2499cc4130b06ff7c26f41c00f04" ),
                        entry( "sysInfo",
                                "Darwin erh2.10gen.cc 9.6.0 Darwin Kernel Version 9.6.0: Mon Nov 24 17:37:00 PST "
                                        + "2008; root:xnu-1228.9.59~1/RELEASE_I386 i386 BOOST_LIB_VERSION=1_40" ),
                        entry( "bits", 64 ), entry( "debug", false ), entry( "maxBsonObjectSize", 16777216 ),
                        entry( "ok", 1.0 ) ) );
        return reply;
    }
}
