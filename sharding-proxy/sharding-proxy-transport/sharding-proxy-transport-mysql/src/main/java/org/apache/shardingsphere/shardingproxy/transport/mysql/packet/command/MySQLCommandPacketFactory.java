/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.shardingproxy.backend.communication.jdbc.connection.BackendConnection;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.admin.MySQLUnsupportedCommandPacket;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.admin.initdb.MySQLComInitDbPacket;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.admin.ping.MySQLComPingPacket;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.admin.quit.MySQLComQuitPacket;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.query.binary.close.MySQLComStmtClosePacket;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.query.binary.execute.MySQLQueryComStmtExecutePacket;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.query.binary.prepare.MySQLComStmtPreparePacket;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.query.text.fieldlist.MySQLComFieldListPacket;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.query.text.query.MySQLComQueryPacket;
import org.apache.shardingsphere.shardingproxy.transport.mysql.payload.MySQLPacketPayload;

import java.sql.SQLException;

/**
 * MySQL command packet factory.
 *
 * @author zhangliang
 * @author wangkai
 * @author zhaojun
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MySQLCommandPacketFactory {
    
    /**
     * Create new instance of command packet.
     *
     * @param payload MySQL packet payload
     * @param backendConnection backend connection
     * @return command packet
     * @throws SQLException SQL exception
     */
    public static MySQLCommandPacket newInstance(final MySQLPacketPayload payload, final BackendConnection backendConnection) throws SQLException {
        Preconditions.checkArgument(0 == payload.readInt1(), "Sequence ID of MySQL command packet must be `0`.");
        int commandPacketTypeValue = payload.readInt1();
        MySQLCommandPacketType type = MySQLCommandPacketType.valueOf(commandPacketTypeValue);
        switch (type) {
            case COM_QUIT:
                return new MySQLComQuitPacket();
            case COM_INIT_DB:
                return new MySQLComInitDbPacket(payload, backendConnection);
            case COM_FIELD_LIST:
                return new MySQLComFieldListPacket(payload, backendConnection);
            case COM_QUERY:
                return new MySQLComQueryPacket(payload, backendConnection);
            case COM_STMT_PREPARE:
                return new MySQLComStmtPreparePacket(backendConnection, payload);
            case COM_STMT_EXECUTE:
                return new MySQLQueryComStmtExecutePacket(payload, backendConnection);
            case COM_STMT_CLOSE:
                return new MySQLComStmtClosePacket(payload);
            case COM_PING:
                return new MySQLComPingPacket();
            default:
                return new MySQLUnsupportedCommandPacket(type);
        }
    }
}
