$(document).ready(function(){
    console.log("1")
    $('#schema_class_section').hide();
    $('#schema_property_section').hide();
    $('#home-section').show(); 
    // $('#footer').html(getFooterContent())
});

// function getFooterContent(){

// return `
// <p>&copy; 2020 <a href="https://iudx.org.in" target="_blank">IUDX</a></p>
// `

// }

function showAllProperties(){
    console.log("2")
    $('#home-section').hide();
    $('#schema_class_section').hide();
    $('#schema_property_section').show();
}

function showAllClasses(){
    $('#home-section').hide();
    $('#schema_property_section').hide();
    $('#schema_class_section').show();
    $('#footer').show();
        
   
}
function getSchemaClass(__data){
    console.log("called schemaclass")
    return `
    <tr>           
                        <th class="prop-nam" scope="row">
      
                        <code property="rdfs:label"><a href="`+ "/" +__data[i]['rdfs:label']+`" onclick='show_property('`+__data[i]['rdfs:label']+`','`+__data[i]['rdfs:comment']+`')'>`+__data[i]['rdfs:label']+`</a></code>
                        </th>
                        <td>&nbsp;</td>
                        <td class="prop-desc" property="rdfs:comment">`+__data[i]['rdfs:comment']+`</td></tr><tr typeof="rdfs:Property" resource="http://schema.org/addressCountry">
          
        
                    </tr>
                         `

}

function getSchemaProperties(__data){
    return `
   
    <tr>           
    <th class="prop-nam" scope="row">

<code property="rdfs:label"><a href="./address">`+__data[i]['rdfs:label']+`</a></code>
  </th>

<td class="prop-desc" property="rdfs:comment">`+__data[i]['rdfs:comment']+`</td></tr><tr typeof="rdfs:Property" resource="http://schema.org/addressCountry">
  

</tr>
            `
}

function show_property(class_label,class_desc){
    console.log("print")
    // var schema = "Schema:";
    var proxy = 'https://cors-anywhere.herokuapp.com/';

    //  document.location.href = 'https://localhost.iudx.org.in:8443/schema_property';
    $.ajax({
        url: proxy + "https://voc.iudx.org.in/"+ class_label,
        type: 'GET',
        contentType: 'application/json+ld',
        success: function (data) {
           console.log(data);
           for(i=0;i<data.length;i++){
            $('#propertySchema').append(`
            <tr>           
    <th class="prop-nam" scope="row">

<code property="rdfs:label"><a href="./address">`+data[i]['rdfs:label']+`</a></code>
  </th>
<td class="prop-ect">
<link property="rangeIncludes" href="http://schema.org/PostalAddress"><a href="./PostalAddress">PostalAddress</a>&nbsp; or <br> <link property="rangeIncludes" href="http://schema.org/Text"><a href="./Text">Text</a>&nbsp;<link property="domainIncludes" href="http://schema.org/GeoCoordinates"><link property="domainIncludes" href="http://schema.org/GeoShape"><link property="domainIncludes" href="http://schema.org/Organization"><link property="domainIncludes" href="http://schema.org/Person"><link property="domainIncludes" href="http://schema.org/Place"></td>
<td class="prop-desc" property="rdfs:comment">`+data[i]['rdfs:comment']+`</td></tr><tr typeof="rdfs:Property" resource="http://schema.org/addressCountry">
  

</tr>

            `)

            }


        },
        error: function (error) {
            
        }
    });
    
}