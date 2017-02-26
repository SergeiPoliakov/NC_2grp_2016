/**
 * Created by Lawrence on 25.02.2017.
 */


(function() {

    var app = {

        initialize : function() {
            this.setUpListeners();
        },


        setUpListeners : function() {
            $('form').on('submit', app.submitForm);
            $('form').on('keydown', 'input', app.removeError);
        },

        submitForm : function(e) {
            var form = $(this);

            if (app.validateForm(form) === false ) return false;

            $('#success-alert').removeClass('hidden');

            console.log('go in ajax');
        },

        validateForm : function(form) {
            var inputs = form.find('input'),
                valid = true;

            inputs.tooltip('destroy');

            $.each(inputs, function(index, val) {
                var input = $(val),
                   vala = input.val(),
                    formGroup = input.parents('.form-group'),
                    glyphicon = formGroup.find('.form-control-feedback'),
                    label = formGroup.find('label').text().toLowerCase(),
                     textError = 'Поле не должно быть пустым' ;

                if (vala.length === 0) {
                    formGroup.addClass('has-error').removeClass('has-success');
                    glyphicon.addClass('glyphicon-remove').removeClass('glyphicon-ok');
                    input.tooltip({
                        trigger: 'manual',
                        placement: 'right',
                       title: textError
                    }).tooltip('show');
                    valid = false
                } else {
                    formGroup.addClass('has-success').removeClass('has-error');
                    glyphicon.addClass('glyphicon-ok').removeClass('glyphicon-remove')
                }
            });
            return valid;
        },

        removeError: function () {
            $(this).tooltip('destroy').parent('.form-group').removeClass('has-error');
        }

    };

    app.initialize();

}());