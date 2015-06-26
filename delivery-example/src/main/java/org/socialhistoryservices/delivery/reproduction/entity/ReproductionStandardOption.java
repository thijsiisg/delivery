package org.socialhistoryservices.delivery.reproduction.entity;

import org.hibernate.validator.constraints.NotBlank;
import org.socialhistoryservices.delivery.record.entity.ExternalRecordInfo;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
		LEVEL1,
		LEVEL2,
		LEVEL3
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
	@Column(name = "optionName", nullable = false)
	private String optionName;

	/**
	 * Get the option name.
	 *
	 * @return the option name.
	 */
	public String getOptionName() {
		return optionName;
	}

	/**
	 * Set the option name.
	 *
	 * @param optionName the option name.
	 */
	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}

	@NotBlank
	@Size(max = 255)
	@Column(name = "optionDescription", nullable = false)
	private String optionDescription;

	/**
	 * Get the option description.
	 *
	 * @return the option description.
	 */
	public String getOptionDescription() {
		return optionDescription;
	}

	/**
	 * Set the option description.
	 *
	 * @param optionDescription the option description.
	 */
	public void setOptionDescription(String optionDescription) {
		this.optionDescription = optionDescription;
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
		this.price = price;
	}

	@NotNull
	@Min(0)
	@Column(name = "deliveryTime", nullable = false)
	private int deliveryTime;

	/**
	 * Get the delivery time in days.
	 *
	 * @return the delivery time in days.
	 */
	public int getDeliveryTime() {
		return deliveryTime;
	}

	/**
	 * Set the delivery time in days.
	 *
	 * @param deliveryTime the delivery time in days.
	 */
	public void setDeliveryTime(int deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
}
