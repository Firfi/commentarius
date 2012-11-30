
var _commentarius_mainjs = function() {
       jQuery("#commentarius-panel").ready(loadFunction);
       function loadFunction () {
            toggleIfSelected();
            var changeToDefault = false;
            commselect = document.getElementById('commentarius_grade_select');
            if (_commentarius_mainjs.gradePropertyValue !== '') {
                var found = false;
                for (var i = 0; i < commselect.length; ++i) {
                    elem = commselect[i];
                    if (elem.text == _commentarius_mainjs.gradePropertyValue) {
                         commselect.selectedIndex = elem.index;
                         found = true;
                         break;
                    }
                }
                if (!found && _commentarius_mainjs.gradePropertyValue.length !== 0) {
                    changeToDefault = true;
                }
            }
            setTimeout(function(){jQuery('#commentarius_grade_select').children()[commselect.selectedIndex].selected = true;},10);// so, javascript so javascript

            commselect_oldindex = commselect.selectedIndex;
            var commselect_oldvalue = commselect.children[commselect_oldindex].text;
            initCommentDiv(commselect_oldvalue);
            commselect.onchange = function () {
                 commselect_newindex = commselect.selectedIndex;
                 var commselect_newvalue;
                 var commselect_id;
                 if (commselect_newindex == 0) {
                    commselect_id = 0;
                    commselect_newvalue = '';
                 }
                 else {
                    commselect_newvalue = commselect.children[commselect_newindex].text;
                    commselect_id = commselect.children[commselect_newindex].value;
                 }


                 AJS.$.ajax(
                 {
                    url: contextPath + "/rest/commentarius/latest/mpsuserproperty/update/" + _commentarius_mainjs.username,
                    type: "POST",
                    success: function(msg) {
                        commselect_oldvalue = msg.newProperty;
                        notificateUser(msg.newProperty);
                    },
                    data: JSON.stringify({
                        key: _commentarius_mainjs.gradePropertyKey,
                        oldValue: commselect_oldvalue,
                        newValue: commselect_newvalue,
                        ID: commselect_id
                    }),
                    contentType: 'application/json',
                 });
            }
            if (changeToDefault) {
                commselect.onchange();
            }
            function toggleIfSelected() {
                if (_commentarius_mainjs.gradePropertyValue.length !== 0 &&
                _commentarius_mainjs.gradePropertyValue !== "") {
                    AJS.$("#commentarius-panel").removeClass("expanded").addClass("collapsed");
                }
            }
            function initCommentDiv(oldvalue) {
                jQuery('div#addcomment').find('div.mod-content').append('<div id="commentarius-comment-notification"></div>');
                notificateUser(oldvalue);
            }
            function notificateUser(str) {
                comdiv = jQuery('div#commentarius-comment-notification');
                notif = comdiv.find('p');
                if (!notif.length) { notif = comdiv.append('<p/>') };
                notif[0].innerText = "Моя должность в автоматической подписи : " + str;
            }
         };
};


var _shablonius_mainjs = function() {
    var SEPARATOR = '\n\n';
    jQuery(document).ready(function() {
        if (jQuery('#shablonius-main').length > 0) return;
        jQuery('#footer-comment-button').click(function(){
            //jQuery('#comment-preview_link').hide();
            //jQuery('#comment').hide();
            //jQuery('#comment-issue').parent('li').hide();
            //jQuery('#issue-comment-add-submit').click(fillMainComment);
        });
        var shabloniusMainDiv = jQuery('<div/>', {
            id : 'shablonius-main'
        }).css('clear','left');
        /* shabloniusMainDiv.append(
                  'Выбор шаблона сообщения : '
                + '<select name="shablonius_template_select" id="shablonius-template-select">'
                + '<option value="">Номер шаблона : </option>'
                + '</select>'
                + 'Название :'
        );
        var templateTitleTextArea = jQuery('<input/>',{
            id: "shablonius-save-label"
        });
        shabloniusMainDiv.append(templateTitleTextArea);
        shabloniusMainDiv.append('<input type="submit" class="button" id="shablonius-save-button" name="Save" value="Сохранить"></input>'
        + '</div>'
        +'</br>'); */

        jQuery('div#addcomment').find('div.mod-content').append(shabloniusMainDiv);

        initHeaderBodyFooterHTML();
        /* var previewElement = jQuery('<textarea/>', {
             id : 'shabloniusPreview',
             disabled : true
         }).css('background-color','#C8C8C8');
        shabloniusMainDiv.append(previewElement);*/
        //areas = [headerTextArea,bodyTextArea,footerTextArea];

        /* for (var i = 0; i < areas.length; ++i) {
            var area = jQuery(areas[i]);
            area.change(fillCommentAndPreview);
            area.keyup(fillCommentAndPreview);
        } */
        /*jQuery.each([
        bodyElementSelect, bodyTextArea,
        ], function(i, v) {
           v.change(fillCommentAndPreview);
        });

        createUserTemplateOptions(); */
        createOptions();
    });

    function fillCommentAndPreview() {
        fillMainComment();
        //fillPreview();
    }

    function fillMainComment() {
        var finalComment = /* jQuery('#hh_ta').val() + SEPARATOR + */ jQuery('#hb_ta').val()
            /* + SEPARATOR +
            jQuery('#hf_ta').val(); */
        var finalCommentArea = jQuery('#comment');
        finalCommentArea.val(finalComment);
        finalCommentArea.keyup();
    }

    function fillPreview() {
        var preview = jQuery('#shabloniusPreview');
        var comment = jQuery('#comment');
        preview.val(comment.val());
        fitContent(shabloniusPreview, 800);
        comment.keyup();

        function fitContent(id, maxHeight)
        {
           var text = id && id.style ? id : document.getElementById(id);
           if ( !text )
              return;

           /* Accounts for rows being deleted, pixel value may need adjusting */
           if (text.clientHeight == text.scrollHeight) {
              text.style.height = "30px";
           }

           var adjustedHeight = text.clientHeight;
           if ( !maxHeight || maxHeight > adjustedHeight )
           {
              adjustedHeight = Math.max(text.scrollHeight, adjustedHeight);
              if ( maxHeight )
                 adjustedHeight = Math.min(maxHeight, adjustedHeight);
              if ( adjustedHeight > text.clientHeight )
                 text.style.height = adjustedHeight + "px";
           }
        }
    }

    function createUserTemplateOptions() {
        var selectSelect = jQuery('#shablonius-template-select');
        var saveLabel = jQuery('#shablonius-save-label');
        var saveButton = jQuery('#shablonius-save-button');
        var selectedIndex = 0;
        AJS.$.ajax(
         {
            url: contextPath + "/rest/commentarius/latest/mpstemplatemessage/get/user/" + AJS.params.loggedInUser,
            type: "GET",
            success: function(msg) {
                var existMessages = new Array();
                if (msg.length >= 1) {
                    for (var i = 0; i < msg.length; ++i) {
                        existMessages[msg[i].number] = msg[i];
                    }
                }
                for (var i = 0; i < 10; ++i) {
                    var optionlabel = i;
                    if (existMessages[i]) optionlabel = optionlabel + ": " + existMessages[i].label;
                    var opt = jQuery("<option/>", {
                        label : optionlabel,
                        text : optionlabel,
                        value : i
                    });
                    selectSelect.append(opt);
                }
                var selectedOption = selectSelect.find(":selected");
                selectSelect.change(fillCommentParts);
                createSaveTemplateListener();
                function fillCommentParts() {
                    selectedOption = selectSelect.find(":selected");
                    selectedIndex = parseInt(selectedOption.val());
                    var messageObject = existMessages[selectedIndex];
                    if (messageObject) {
                        //jQuery('#hh_ta').val(messageObject.header);
                        jQuery('#hb_ta').val(messageObject.body);
                        //jQuery('#hf_ta').val(messageObject.footer);
                        saveLabel.val(messageObject.label);
                    }
                    fillCommentAndPreview();
                }
                function createSaveTemplateListener() {
                    saveButton.click(function() {
                        selectedOption.attr('label', selectedIndex + ": " + saveLabel.val());
                        selectedOption.text(selectedIndex + ": " + saveLabel.val());
                        fillMainComment();
                        var headval = jQuery('#hh_ta').val();
                        var bodyval = jQuery('#hb_ta').val();
                        var footval = jQuery('#hf_ta').val();
                        AJS.$.ajax(
                         {
                            url: contextPath + "/rest/commentarius/latest/mpstemplatemessage/post/user/message",
                            type: "POST",
                            data: JSON.stringify({
                                  username: AJS.params.loggedInUser,
                                  number: selectedIndex,
                                  label: saveLabel.val(),
                                  header: headval,
                                  body: bodyval,
                                  footer: footval
                              }),
                            success: function(msg) {
                                  updateExistMessage(selectedIndex);
                            },
                            contentType: 'application/json'
                         });

                         function updateExistMessage(selectedIndex) {
                            var newMessage = {
                                header : headval,
                                body : bodyval,
                                footer : footval,
                                label : saveLabel.val(),
                                number : selectedIndex,
                                username : AJS.params.loggedInUser
                            };
                            var oldMessage = existMessages[selectedIndex];
                            if (!oldMessage) {
                                existMessages[selectedIndex] = newMessage;
                            } else {
                                for (var attrname in newMessage) {
                                    oldMessage[attrname] = newMessage[attrname];
                                }
                            }
                         }
                    });
                }

            }
         });

    }
    var headerElementSelect;
    var headerTextArea;
    var bodyElementSelect;
    var bodyTextArea;
    var footerElementSelect;
    var footerTextArea;
    function initHeaderBodyFooterHTML() {
        var defaultOptionText = "Выберите вариант";
        function createFirstOption() {
            return jQuery('<option/>',{
                value : "",
                text : defaultOptionText
            });
        }
        /* headerElementSelect = jQuery('<select/>',{
             name : 'head_to_answer',
             id : 'head_to_answer',
        }).append(createFirstOption());
        headerTextArea = jQuery('<textarea/>',{
             id : 'hh_ta'
         });*/

        jQuery('div#shablonius-main').append('<div id="commentarius-templates-selections"/>');
        var templatesSelections = jQuery('#commentarius-templates-selections');
        templatesSelections.css('clear','left');

        /* templatesSelections.append('Заголовок: ').append(headerElementSelect)
        .append(createAddButton(headerElementSelect,headerTextArea))
        .append(headerTextArea).append('<br/>');
        */

        bodyElementSelect = jQuery('<select/>',{
             name : 'body_to_answer',
             id : 'body_to_answer',
        }).append(createFirstOption());
        /*bodyTextArea = jQuery('<textarea/>',{
            id : 'hb_ta'
        }); */
        templatesSelections.append('Шаблон: ').append(bodyElementSelect).append(' ')
        .append(createAddButton(bodyElementSelect,jQuery('#comment')));
        /* .append(bodyTextArea).append('<br/>'); */

        /* footerElementSelect = jQuery('<select/>',{
             name : 'footer_to_answer',
             id : 'footer_to_answer',
             change : function(e) {
                var select = e.currentTarget;
                footerTextArea.val(injectFooterVariables(select[select.selectedIndex].value));
             }
        }).append(createFirstOption());
        jQuery('#commentarius_grade_select').change(function() {
            footerElementSelect.change();
        });
        footerTextArea = jQuery('<textarea/>',{
             id : 'hf_ta',
             disabled : true
         }).css('background-color','#C8C8C8')
        templatesSelections.append('Подпись: ').append(footerElementSelect)
        .append(footerTextArea).append('<br/>'); */

    }

    function createAddButton(from, to) {
        var selectedOnce = false;
        var result = jQuery('<input/>',{
            type : 'image',
            src : contextPath + '/images/icons/newfeature.gif',
            click : function() {
                if (from.val() === '') return;
                var separator = (to.val().trim() === '' ? '' : '\n');
                to.val(to.val() + separator + from.val());
                to.change();
            }
        });
        from.change(function() {
             if (!selectedOnce) {
                 selectedOnce = true;
                 result.click();
             }
         });
        return result;
    }

    function injectFooterVariables(text) {
        var rx = /\{0\}/;
        var var0 = document.getElementById('commentarius_grade_select');
        var0 = var0[var0.selectedIndex].value;
        return text.replace(rx,var0);;
    }

    function createOptions()
    {
        getElementsOfType("header", jQuery('select#head_to_answer'));
        getElementsOfType("body", jQuery('select#body_to_answer'));
        getElementsOfType("footer", jQuery('select#footer_to_answer'));
        function getElementsOfType(type, el) {
            AJS.$.ajax(
             {
                url: contextPath + "/rest/commentarius/latest/mpstemplatemessage/get/" + type,
                type: "GET",
                success: function(msg) {
                    if ( msg.length >= 1 )
                    {
                        for ( var i=0; i<msg.length; i++ )
                        {
                            var opt = jQuery('<option/>', {
                                label : msg[i].small,
                                text : msg[i].small,
                                value : msg[i].full
                            })
                            el.append(opt);
                        }
                    }
                }
             });
             //el.change(fillMainComment);
        }
    }
}

