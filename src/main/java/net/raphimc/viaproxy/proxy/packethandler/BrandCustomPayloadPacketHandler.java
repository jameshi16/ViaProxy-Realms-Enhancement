/*
 * This file is part of ViaProxy - https://github.com/RaphiMC/ViaProxy
 * Copyright (C) 2021-2024 RK_01/RaphiMC and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.raphimc.viaproxy.proxy.packethandler;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.util.Key;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import net.raphimc.netminecraft.packet.PacketTypes;
import net.raphimc.netminecraft.packet.UnknownPacket;
import net.raphimc.viaproxy.proxy.session.ProxyConnection;

import java.util.List;

public class BrandCustomPayloadPacketHandler extends CustomPayloadPacketHandler {

    private static final String BRAND_CHANNEL = "minecraft:brand";
    private static final String LEGACY_BRAND_CHANNEL = "MC|Brand";

    public BrandCustomPayloadPacketHandler(ProxyConnection proxyConnection) {
        super(proxyConnection);
    }

    @Override
    public ByteBuf handleP2S(UnknownPacket packet, String channel, ByteBuf data, List<ChannelFutureListener> listeners) throws Exception {
        if (Key.namespaced(channel).equals(BRAND_CHANNEL) || channel.equals(LEGACY_BRAND_CHANNEL)) {
            String brand;
            try {
                brand = PacketTypes.readString(data, Short.MAX_VALUE);
            } catch (Exception e) {
                if (this.proxyConnection.getServerVersion().newerThan(ProtocolVersion.v1_20)) {
                    throw e;
                } else { // <=1.20 clients ignore errors
                    brand = "Unknown";
                }
            }
            final String newBrand = "ViaProxy (" + this.proxyConnection.getClientVersion().getName() + ") -> " + brand + " §r(" + this.proxyConnection.getServerVersion().getName() + ")";

            final ByteBuf newData = Unpooled.buffer();
            PacketTypes.writeString(newData, newBrand);
            return newData;
        }

        return super.handleP2S(packet, channel, data, listeners);
    }

}
