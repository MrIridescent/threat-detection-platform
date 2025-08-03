package com.mriridescent.threatdetection.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents a network packet for analysis.
 */
@Data
public class NetworkPacket {
    private String packetId;
    private String sourceIp;
    private int sourcePort;
    private String destinationIp;
    private int destinationPort;
    private String protocol;
    private int size;
    private String payload;
    private LocalDateTime timestamp;
    private String interfaceName;
}
