
package com.services.billingservice.model.compliance;

import com.services.billingservice.model.Approvable;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "comp_reksadana")
public class Reksadana extends Approvable {
    @Id
    @Column(name = "code")
    //Short Code
    private String code;

    @Column(name = "reksadana_name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "email")
    private String email;

    @Column(name = "manajer_investasi")
    private String manajerInvestasi;

    @Column(name = "pic")
    private String pic;

    //Long Code
    @Column(name = "external_code")
    private String externalCode;

    //e.g: MM, FI, MX, etc.
//    @ManyToOne
//    @JoinColumn(name="reksadana_type", nullable=false)
//    private ReksadanaType reksadanaType;

    @Column(name = "isSyariah")
    private boolean syariah;

    @Column(name = "isConventional")
    private boolean conventional;

    @Column(name = "tnab_minimum")
    private double tnabMinimum;

    //Modal disetor <5%
    @Column(name = "percentage_modal")
    private double percentageModal;

    @Column(name = "isDelete")
    private boolean delete;
}
