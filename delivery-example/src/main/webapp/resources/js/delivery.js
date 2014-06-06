/*
 * Copyright (C) 2013 International Institute of Social History
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

$(document).ready(function(){
    $(".filter_date").datepicker({
        "dateFormat": "yy-mm-dd"
    });

    $(".permission_form .date").datepicker({
        "dateFormat": "yy-mm-dd",
        "minDate": new Date()
    });

    $(".record_form .date").datepicker({
        "dateFormat": "yy-mm-dd",
        "changeYear": true,
        "yearRange": '+0:+100'
    });

    $(".selectAll").click(function() {
        $(".checkItem").attr("checked", true);
    });

    $(".selectNone").click(function() {
        $(".checkItem").attr("checked", false);
    });

    $("#scanid").focus();
});

function toggleField(infoId, fieldId, link, hideTxt, showTxt) {
    var field = $("#"+fieldId);
    var info = $("#"+infoId);

    if (field.hasClass("hidden")) {
        if (field.data("oldData") != null) {
            field.val(field.data("oldData"));
        } else {
            field.val(info.val());
        }
        info.addClass("hidden");
        field.removeClass("hidden");
        field.focus();
        $(link).html(hideTxt);
    } else {
        field.addClass("hidden");
        info.removeClass("hidden");
        field.data("oldData", field.val());
        field.val('');
        $(link).html(showTxt);

    }

}

function toggleFieldsWithClass(infoClass, fieldClass, link, hideTxt, showTxt) {
    var fields = $("."+fieldClass);
    var infos = $("."+infoClass);
    if (fields.hasClass("hidden")) {

        infos.addClass("hidden");

        fields.each(function(index) {
            if ($(this).data('oldData') != null) {
                $(this).val($(this).data('oldData'));
            } else {
                var info = $("#"+$(this).attr("id")+"Info");
                $(this).val(info.val());
            }


        });
        fields.removeClass("hidden");
        fields.first().focus();
        $(link).html(hideTxt);

    } else {
        fields.addClass("hidden");
        infos.removeClass("hidden");
        fields.each(function(index) {
           $(this).data('oldData', $(this).val());
        });
        fields.val('');
        $(link).html(showTxt);
    }
}

function renumberHoldings() {
    var rows = $("table.holdings tr.holding");

    var newIdx = 0;
    rows.each(function(index) {
        var curIdx = $(this).attr('id').substr(7);
        var curPrefix = "holdings\\["+curIdx+"\\]\\.";
	    var curPrefix2 = "holdings"+curIdx+"\\.";
        var newPrefix = "holdings["+newIdx+"].";
	    var newPrefix2 = "holdings"+newIdx+".";
        $(this).attr('id', "holding"+newIdx);

        $("#"+curPrefix+"signature").attr('id', newPrefix+"signature")
                                    .attr('name', newPrefix+"signature");
        $("#"+curPrefix2+"direction").attr('id', newPrefix2+"direction")
                                    .attr('name', newPrefix+"direction");
        $("#"+curPrefix2+"floor").attr('id', newPrefix2+"floor")
                                .attr('name', newPrefix+"floor");
        $("#"+curPrefix2+"cabinet").attr('id', newPrefix2+"cabinet")
                                  .attr('name', newPrefix+"cabinet");
        $("#"+curPrefix2+"shelf").attr('id', newPrefix2+"shelf")
                                .attr('name', newPrefix+"shelf");
        $("#"+curPrefix+"usageRestriction").attr('id', newPrefix+"usageRestriction")
                                           .attr('name', newPrefix+"usageRestriction");
        newIdx++;
    });
}

function addNewHolding() {
    var rows = $("table.holdings tr.holding");
    var curIdx = -1;
    if (rows.size() > 0) {
        curIdx = parseInt(rows.last().attr('id').substr(7));
    }
    var newPrefix = "holdings["+(curIdx+1)+"].";
    var newHolding = $("#newHolding").clone();
    newHolding.removeClass("hidden");
    newHolding.addClass("holding");
    newHolding.attr('id', "holding"+(curIdx+1));
    newHolding.find("#holdings\\.new\\.signature").attr('id', newPrefix+"signature")
                                    .attr('name', newPrefix+"signature");
    newHolding.find("#holdings\\.new\\.direction").attr('id', newPrefix+"direction")
                                    .attr('name', newPrefix+"direction");
    newHolding.find("#holdings\\.new\\.floor").attr('id', newPrefix+"floor")
                                    .attr('name', newPrefix+"floor");
    newHolding.find("#holdings\\.new\\.cabinet").attr('id', newPrefix+"cabinet")
                                    .attr('name', newPrefix+"cabinet");
    newHolding.find("#holdings\\.new\\.shelf").attr('id', newPrefix+"shelf")
                                    .attr('name', newPrefix+"shelf");
    newHolding.find("#holdings\\.new\\.usageRestriction").attr('id', newPrefix+"usageRestriction")
                                    .attr('name', newPrefix+"usageRestriction");
     
    newHolding.appendTo("table.holdings");


}

function addNewHoldingReservation(newHoldingReservation) {
    var rows = $("#holdingReservations .holdingReservations");
    var curIdx = -1;
    if (rows.size() > 0) {
        curIdx = parseInt(rows.last().attr('id').substr(18));
    }
    var newPrefix = "holdingReservations["+(curIdx+1)+"].";

    $('#newHoldingReservation .removeButton').clone().prependTo(newHoldingReservation);

    newHoldingReservation.addClass("holdingReservations");
    newHoldingReservation.attr('id', "holdingReservation"+(curIdx+1));
    newHoldingReservation.find(".holding").attr('id', newPrefix+"holding")
                                    .attr('name', newPrefix+"holding");
    var cl = newHoldingReservation.find(".commentlist");
    cl.removeClass("hidden");
    cl.find(".comment").attr('id', newPrefix+"comment")
                                    .attr('name', newPrefix+"comment");

    newHoldingReservation.find('.addButton').remove();
    newHoldingReservation.appendTo("#holdingReservations");
}

function removeNewHoldingReservation(newHoldingReservation) {
    $('#newHoldingReservation .addButton').clone().prependTo(newHoldingReservation);
    newHoldingReservation.attr('id', "");
    newHoldingReservation.find(".holding").attr('id', "")
                                     .attr('name', "");

    var cl = newHoldingReservation.find(".commentlist");
    cl.addClass("hidden");
    cl.find(".comment").attr('id', "").attr('name', "");
    
    newHoldingReservation.find('.removeButton').remove();
    newHoldingReservation.removeClass("holdingReservations");
    newHoldingReservation.appendTo("#holdingSearch");
}

function renumberHoldingReservations() {
    var rows = $("#holdingReservations .holdingReservations");

    var newIdx = 0;
    rows.each(function(index) {
        var curIdx = $(this).attr('id').substr(18);
        var curPrefix = "holdingReservations\\["+curIdx+"\\]\\.";
        var newPrefix = "holdingReservations["+newIdx+"].";
        $(this).attr('id', "holdingReservation"+newIdx);

        $("#"+curPrefix+"holding").attr('id', newPrefix+"holding")
                                    .attr('name', newPrefix+"holding");
        newIdx++;
    });
}
