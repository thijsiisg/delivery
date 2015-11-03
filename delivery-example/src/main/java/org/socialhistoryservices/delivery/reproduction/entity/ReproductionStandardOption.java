package org.socialhistoryservices.delivery.reproduction.entity;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.socialhistoryservices.delivery.record.entity.ExternalRecordInfo;

import javax.persistence.*;
import java.math.BigDecimal;
import javax.validation.constraints.*;

/**
 * ReproductionOption object representing a ReproductionStandardOption.
 */
@Entity
@Table(name = "reproduction_standard_options")
@Configurable
public class ReproductionStandardOption {
    /**
     * Level of material access of the reproduction in the SOR.
     */
    public enum Level {
        MASTER,
        LEVEL1
    }

    /**
     * The ReproductionStandardOption's id.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    /**
     * Get the ReproductionStandardOption's id.
     *
     * @return the ReproductionStandardOption's id.
     */
    public int getId() {
        return id;
    }

    /**
     * Set the ReproductionStandardOption's id.
     *
     * @param idthe ReproductionStandardOption's id.
     */
    public void setId(int id) {
        this.id = id;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "material_type", nullable = false)
    private ExternalRecordInfo.MaterialType materialType;

    /**
     * Get the material type.
     *
     * @return The material type.
     */
    public ExternalRecordInfo.MaterialType getMaterialType() {
        return materialType;
    }

    /**
     * Set the material type.
     *
     * @param type The material type.
     */
    public void setMaterialType(ExternalRecordInfo.MaterialType type) {
        materialType = type;
    }

    @NotBlank
    @Size(max = 50)
    @Column(name = "optionName_en", nullable = false)
    private String optionNameEN;

    @NotBlank
    @Size(max = 50)
    @Column(name = "optionName_nl", nullable = false)
    private String optionNameNL;

    /**
     * Get the option name.
     *
     * @return the option name.
     */
    public String getOptionName() {
        if (LocaleContextHolder.getLocale().getLanguage().equals("nl"))
            return optionNameNL;
        return optionNameEN;
    }

    /**
     * Get the option name (Dutch).
     *
     * @return the option name in Dutch.
     */
    public String getOptionNameNL() {
        return optionNameNL;
    }

    /**
     * Get the option name (English).
     *
     * @return the option name in English.
     */
    public String getOptionNameEN() {
        return optionNameEN;
    }

    /**
     * Set the option name (Dutch).
     *
     * @param optionName the option name in Dutch.
     */
    public void setOptionNameNL(String optionName) {
        this.optionNameNL = optionName;
    }

    /**
     * Set the option name (English).
     *
     * @param optionName the option name in English.
     */
    public void setOptionNameEN(String optionName) {
        this.optionNameEN = optionName;
    }

    @NotBlank
    @Size(max = 255)
    @Column(name = "optionDescription_nl", nullable = false)
    private String optionDescriptionNL;

    @NotBlank
    @Size(max = 255)
    @Column(name = "optionDescription_en", nullable = false)
    private String optionDescriptionEN;

    /**
     * Get the option description.
     *
     * @return the option description.
     */
    public String getOptionDescription() {
        if (LocaleContextHolder.getLocale().getLanguage().equals("nl"))
            return optionDescriptionNL;
        return optionDescriptionEN;
    }

    /**
     * Get the option description (Dutch).
     *
     * @return the option description in Dutch.
     */
    public String getOptionDescriptionNL() {
        return optionDescriptionNL;
    }

    /**
     * Get the option description (English).
     *
     * @return the option description in English.
     */
    public String getOptionDescriptionEN() {
        return optionDescriptionEN;
    }

    /**
     * Set the option description (Dutch).
     *
     * @param optionDescription the option description in Dutch.
     */
    public void setOptionDescriptionNL(String optionDescription) {
        this.optionDescriptionNL = optionDescription;
    }

    /**
     * Set the option description (DutEnglishch).
     *
     * @param optionDescription the option description in English.
     */
    public void setOptionDescriptionEN(String optionDescription) {
        this.optionDescriptionEN = optionDescription;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private Level level;

    /**
     * Get the SOR access level.
     *
     * @return the SOR access level.
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Set the SOR access level.
     *
     * @param level the SOR access level.
     */
    public void setLevel(Level level) {
        this.level = level;
    }

    @NotNull
    @Min(0)
    @Digits(integer = 5, fraction = 2)
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    /**
     * Get the price.
     *
     * @return the price.
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Set the price.
     *
     * @param price the price.
     */
    public void setPrice(BigDecimal price) {
        this.price = price.setScale(2);
    }

    @NotNull
    @Min(0)
    @Column(name = "deliveryTime", nullable = false)
    private Integer deliveryTime;

    /**
     * Get the delivery time in days.
     *
     * @return the delivery time in days.
     */
    public Integer getDeliveryTime() {
        return deliveryTime;
    }

    /**
     * Set the delivery time in days.
     *
     * @param deliveryTime the delivery time in days.
     */
    public void setDeliveryTime(Integer deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    @Min(0)
    @Column(name = "minNumberOfPages")
    private Integer minNumberOfPages;

    /**
     * Get the min number of pages for which this option is valid.
     *
     * @return the min number of pages for which this option is valid.
     */
    public Integer getMinNumberOfPages() {
        return minNumberOfPages;
    }

    /**
     * Set the min number of pages for which this option is valid.
     *
     * @param minNumberOfPages the min number of pages for which this option is valid.
     */
    public void setMinNumberOfPages(Integer minNumberOfPages) {
        this.minNumberOfPages = minNumberOfPages;
    }

    @Min(0)
    @Column(name = "maxNumberOfPages")
    private Integer maxNumberOfPages;

    /**
     * Get the max number of pages for which this option is valid.
     *
     * @return the max number of pages for which this option is valid.
     */
    public Integer getMaxNumberOfPages() {
        return maxNumberOfPages;
    }

    /**
     * Set the max number of pages for which this option is valid.
     *
     * @param maxNumberOfPages the max number of pages for which this option is valid.
     */
    public void setMaxNumberOfPages(Integer maxNumberOfPages) {
        this.maxNumberOfPages = maxNumberOfPages;
    }

    @NotNull
    @Min(0)
    @Digits(integer = 5, fraction = 2)
    @Column(name = "copyrightPrice", nullable = false)
    private BigDecimal copyrightPrice = BigDecimal.ZERO;

    /**
     * Get the additional copyright price.
     *
     * @return the additional copyright price.
     */
    public BigDecimal getCopyrightPrice() {
        return copyrightPrice;
    }

    /**
     * Set the additional copyright price.
     *
     * @param copyrightPrice the additional copyright price.
     */
    public void setCopyrightPrice(BigDecimal copyrightPrice) {
        this.copyrightPrice = copyrightPrice;
    }

    @NotNull
    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    /**
     * Get whether this standard option is enabled.
     *
     * @return Whether this standard option is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set whether this standard option is enabled.
     *
     * @param enabled Whether this standard option is enabled.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Merge the other standard option's fields into this standard option.
     *
     * @param other The other reproduction standard option.
     */
    public void mergeWith(ReproductionStandardOption other) {
        setOptionNameNL(other.getOptionNameNL());
        setOptionNameEN(other.getOptionNameEN());
        setOptionDescriptionNL(other.getOptionDescriptionNL());
        setOptionDescriptionEN(other.getOptionDescriptionEN());
        setPrice(other.getPrice());
        setDeliveryTime(other.getDeliveryTime());
        setMinNumberOfPages(other.getMinNumberOfPages());
        setMaxNumberOfPages(other.getMaxNumberOfPages());
        setCopyrightPrice(other.getCopyrightPrice());
        setEnabled(other.isEnabled());
    }
}
