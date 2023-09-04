package io.github.masachi.filter.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

//  `id` int(11) NOT NULL AUTO_INCREMENT,
//          `correlationId` varchar(64) DEFAULT NULL,
//          `userId` varchar(256) DEFAULT NULL,
//          `clientIp` varchar(100) DEFAULT NULL,
//          `area` varchar(100) DEFAULT NULL,
//          `controller` varchar(100) DEFAULT NULL,
//          `action` varchar(100) DEFAULT NULL,
//          `argsParams` text DEFAULT NULL,
//          `responseStatus` int(11) DEFAULT NULL,
//          `errorMsg` text DEFAULT NULL,
//          `requestTime` datetime DEFAULT NULL,
//          `responseTime` datetime DEFAULT NULL,
//          `executionTime` decimal(12,2) DEFAULT NULL,

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class AuditLog {

    private String traceId;

    private String userId;

    private String clientIp;

    private String method;

    private String requestPathname;

    private String argsParams;

    private String responseStatus;

    private String errorMessage;

    private Date requestTime;

    private Date responseTime;

    private BigDecimal executionTime;
}
