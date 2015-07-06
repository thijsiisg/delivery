package org.socialhistoryservices.delivery.reproduction.util;

import org.socialhistoryservices.delivery.reproduction.entity.ReproductionStandardOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a list of reproduction standard options.
 */
public class ReproductionStandardOptions {
    private List<ReproductionStandardOption> options = new ArrayList<ReproductionStandardOption>();

    public ReproductionStandardOptions() {
    }

    public ReproductionStandardOptions(List<ReproductionStandardOption> options) {
        setOptions(options);
    }

    public List<ReproductionStandardOption> getOptions() {
        return options;
    }

    public void setOptions(List<ReproductionStandardOption> options) {
        this.options = options;
    }
}
