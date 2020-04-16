

function getFooterContent(){

return `
<p>&copy; 2020 <a href="https://iudx.org.in" target="_blank">IUDX</a></p>
`

}

function show_classSchema(){
    $('#schema_property_section').hide();
    $('#schema_class_section').show();
}

function show_property(class_schema,class_desc){
    console.log("print")
    var schema = "Schema:";
     document.location.href = 'https://localhost.iudx.org.in:8443/schema_property';
    $.get("https://api.myjson.com/bins/z693g", function (data) {
            // console.log(data['@graph']);
            var dataResult = data['@graph']
            for(i=0;i<dataResult.length;i++){
            if (dataResult[i]['@type'] !== undefined && dataResult[i]['@type'] === 'rdf:Property') {
                console.log(dataResult[i]['schema:domainIncludes'])
                console.log(dataResult[i]['schema:domainIncludes']['@id'] )
                console.log((schema + "" + class_schema))
                if ((dataResult[i]['schema:domainIncludes']['@id'] === (schema + "" + class_schema))){
                    console.log("nnnn")
                    console.log(schema + "" + class_schema)
                    
                        console.log("Hurray")
                    $('#propertySchema').append(`
                    <tr>           
            <th class="prop-nam" scope="row">
      
      <code property="rdfs:label"><a href="./address">`+dataResult[i]['rdfs:label']+`</a></code>
          </th>
       <td class="prop-ect">
      <link property="rangeIncludes" href="http://schema.org/PostalAddress"><a href="./PostalAddress">PostalAddress</a>&nbsp; or <br> <link property="rangeIncludes" href="http://schema.org/Text"><a href="./Text">Text</a>&nbsp;<link property="domainIncludes" href="http://schema.org/GeoCoordinates"><link property="domainIncludes" href="http://schema.org/GeoShape"><link property="domainIncludes" href="http://schema.org/Organization"><link property="domainIncludes" href="http://schema.org/Person"><link property="domainIncludes" href="http://schema.org/Place"></td>
      <td class="prop-desc" property="rdfs:comment">`+dataResult[i]['rdfs:comment']+`</td></tr><tr typeof="rdfs:Property" resource="http://schema.org/addressCountry">
          
        
      </tr>

                    `)
                }
                }

            }
        })

$('#schema_property').append(`
                            <h1 class="page-title">properties of '`+class_schema+`'</h1>
                            <p id="comment">`+class_desc+`</p>
                        `)
}