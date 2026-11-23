package com.solverminds.bl.common.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Composite Primary Key for BlCommodity entity.
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class BlCommodityId implements Serializable {

    private static final long serialVersionUID = 1L;

    private String blNo;
    private String eqpId;
}
