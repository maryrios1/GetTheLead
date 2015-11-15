// magic.js
$(document).ready(function() {
    var form_config = {button: null};
/*
 * <input type="submit" value="StartSearch" id="StartSearch">                                                            
<input type="submit" value="ExportData" id="ExportData">
<input type="submit" value="ShowGraph" id="ShowGraph">
 */
    $("#StartSearch").click(function(){
      form_config.button = 'StartSearch';  
    });

    $("#ExportData").click(function(){
      form_config.button = 'ExportData';  
    });
    
    $("#ShowGraph").click(function(){
      form_config.button = 'ShowGraph';  
    });

    // process the form
    $('form').on('submit',function(event) {
        //var value = $(this).attr('id');
        var value =form_config.button;
        // get the form data
        // there are many ways to get this data using jQuery (you can use the class or id also)
        var formData = {
            'keywords'              : $('input[name=keywords]').val(),
            'NameTable'             : $('input[name=NameTable]').val()
        };

        if (value === 'StartSearch') { 
            // process the form
            $.ajax({
                type        : 'POST', // define the type of HTTP verb we want to use (POST for our form)
                url         : 'SimpleStream', // the url where we want to POST
                data        : formData, // our data object
                dataType    : 'json', // what type of data do we expect back from the server
                encode      : true
            })
            // using the done promise callback
            .done(function(data) {
                // log data to the console so we can see
                console.log(data); 

                // here we will handle errors and validation messages
                if ( ! data.success) 
                {
                    if (data.errors.keywords) {
                           $('#raro').addClass('has-error'); // add the error class to show red input
                           $('#raro').append('<div class="help-block">' + data.errors.name + '</div>'); // add the actual error message under our input
                   }
                }
                else {

                    // ALL GOOD! just show the success message!
                    $('form').append('<div>' + data.message + '</div>');

                    // usually after form submission, you'll want to redirect
                    // window.location = '/thank-you'; // redirect a user to another page
                    alert('success'); // for now we'll just alert the user

                }
            })
            // using the fail promise callback
            .fail(function(data) {

                    // show any errors
                    // best to remove for production
                    console.log(data);
            });
        }
        else
        {
           if (value === 'ExportData') 
           {
               // process the form
            $.ajax({
                type        : 'POST', // define the type of HTTP verb we want to use (POST for our form)
                url         : 'ExportData', // the url where we want to POST
                data        : formData, // our data object
                dataType    : 'json', // what type of data do we expect back from the server
                encode      : true
            })
            // using the done promise callback
            .done(function(data) {
                // log data to the console so we can see
                console.log(data); 

                // here we will handle errors and validation messages
                if ( ! data.success) 
                {
                    if (data.errors.keywords) {
                           $('#raro').addClass('has-error'); // add the error class to show red input
                           $('#raro').append('<div class="help-block">' + data.errors.name + '</div>'); // add the actual error message under our input
                   }
                }
                else {

                    // ALL GOOD! just show the success message!
                    $('form').append('<div>' + data.message + '</div>');

                    // usually after form submission, you'll want to redirect
                    // window.location = '/thank-you'; // redirect a user to another page
                    alert('success'); // for now we'll just alert the user

                }
            })
            // using the fail promise callback
            .fail(function(data) {

                    // show any errors
                    // best to remove for production
                    console.log(data);
            });
           }                           
           else
           {
               //ShowGraph
               alert("Show Graph");
           }
        }
        // stop the form from submitting the normal way and refreshing the page
        event.preventDefault();
    });
    
     $('#frmExportData').on('submit',function(event) {
        var formData = {
            'keywords'              : $('input[name=keywords]').val()
        };
     });

});

$(document).ajaxSend(function(event, request, settings) {
  $('#loading-indicator').show();
});

$(document).ajaxComplete(function(event, request, settings) {
  $('#loading-indicator').hide();
});

