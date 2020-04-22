$(document).ready(function () {
    console.log("1");
    $("#schema_class_section").hide();
    $("#schema_property_section").hide();
    $('#class_property_section').hide();
    $("#home-section").show();
    // $('#footer').html(getFooterContent())
});

// function getFooterContent(){

// return `
// <p>&copy; 2020 <a href="https://iudx.org.in" target="_blank">IUDX</a></p>
// `

// }

function showAllProperties() {
    console.log("2");
    $("#home-section").hide();
    $("#schema_class_section").hide();
    $('#class_property_section').hide();
    $("#schema_property_section").show();
}

function showAllClasses() {
    $("#home-section").hide();
    $("#schema_property_section").hide();
    $("#schema_class_section").show();
    $('#class_property_section').hide();
    $("#footer").show();
}
function getSchemaClass(__data) {
    return (
        `
    <tr>           
                        <th class="prop-nam" scope="row">
      
                        <code property="rdfs:label"><a href="#` +
        __data[i]["rdfs:label"] +
        `" onclick="getClass_property('` +
        __data[i]["rdfs:label"] +
        `','` +
        __data[i]["rdfs:comment"] +
        `');">` +
        __data[i]["rdfs:label"] +
        `</a></code>
                        </th>
                        <td>&nbsp;</td>
                        <td class="prop-desc" property="rdfs:comment">` +
        __data[i]["rdfs:comment"] +
        `</td></tr><tr typeof="rdfs:Property" resource="http://schema.org/addressCountry">
          
        
                    </tr>
                         `
    );
}

function getSchemaProperties(__data) {
    return (
        `
   
    <tr>           
    <th class="prop-nam" scope="row">

<code property="rdfs:label"><a href="./address">` +
        __data[i]["rdfs:label"] +
        `</a></code>
  </th>

<td class="prop-desc" property="rdfs:comment">` +
        __data[i]["rdfs:comment"] +
        `</td></tr><tr typeof="rdfs:Property" resource="http://schema.org/addressCountry">
  

</tr>
            `
    );
}

function getClass_property(class_label, class_desc) {
    // var schema = "Schema:";
    $("#schema_class_section").hide();
    $('#class_property_section').show();
    var proxy = "https://cors-anywhere.herokuapp.com/";

    //  document.location.href = 'https://localhost.iudx.org.in:8443/schema_property';
    $.ajax({
        url: proxy + "https://voc.iudx.org.in/" + class_label,
        type: "GET",
        contentType: "application/json+ld",
        success: function (data) {
            console.log(data["@graph"]);
            var redirectURL = location.origin + "/" + class_label;
            var dataResult_graph = data["@graph"];
            var arr = [];
            for (i = 0; i < dataResult_graph.length; i++) {
                if (
                    dataResult_graph[i]["@type"] !== undefined &&
                    dataResult_graph[i]["@type"][0] === "rdf:Property"
                ) {
                    // console.log(dataResult_graph[i]["@type"]);
                    // console.log(dataResult_graph[i]["iudx:rangeIncludes"])
                    // console.log(dataResult_graph[i]["iudx:rangeIncludes"].length);
                    // for(k=0; k< dataResult_graph[i]["iudx:rangeIncludes"].length;k++)
                    // {
                    //     arr.push(dataResult_graph[i]["iudx:rangeIncludes"][k]['@id'])
                        
                    // }
                    // console.log(arr)
                    for (j = 0; j < dataResult_graph[i]["iudx:domainIncludes"].length; j++) {
                        // console.log("domainIncludes");data
                        if (
                            dataResult_graph[i]["iudx:domainIncludes"][j]["@id"].includes(
                                "iudx:" + class_label
                            )
                        ) {
                            
                            $("#class_propertySchema").append(
                                `
                                <tr>           
                                    <th class="prop-nam" scope="row"><code property="rdfs:label"><a href="./address">` +
                                dataResult_graph[i]["rdfs:label"] +
                                `</a></code>
                                    </th>dataResult_graph
                                   `+getRangeIncludes(dataResult_graph[i]["iudx:rangeIncludes"])+`
                                    <td class="prop-desc" property="rdfs:comment">` +
                                dataResult_graph[i]["rdfs:comment"] +
                                `</td></tr><tr typeof="rdfs:Property" resource="http://schema.org/addressCountry">
                                </tr>
    
                              `
                            );
                            // $(location).attr("href", redirectURL);
                        }
                    }
                }
            }
        },
        error: function (error) { }
    });
}

function getRangeIncludes(_data){
    console.log(_data)
    //  for(i=0;i<_data.length;i++){
    //      console.log(_data[i]['@id'])
    //  }
    if(_data.length === 2){
        
    return `
    <td class="prop-ect"><link property="rangeIncludes" href="http://schema.org/PostalAddress"><a href="./PostalAddress">`+_data[0]['@id']+`</a>&nbsp; or <br> <link property="rangeIncludes" href="http://schema.org/Text"><a href="./Text">`+_data[1]['@id']+`</a>&nbsp;<link property="domainIncludes" href="http://schema.org/GeoCoordinates"><link property="domainIncludes" href="http://schema.org/GeoShape"><link property="domainIncludes" href="http://schema.org/Organization"><link property="domainIncludes" href="http://schema.org/Person"><link property="domainIncludes" href="http://schema.org/Place"></td>
`

}
else if(_data.length === 1){
    return `
    <td class="prop-ect"><link property="rangeIncludes" href="http://schema.org/PostalAddress"><a href="./PostalAddress">`+_data[0]['@id']+`</a></td>
`

}
}