$(document).ready(function () {
    console.log("1");
    $("#schema_class_section").hide();
    $("#schema_property_section").hide();
    $('#class_property_section').hide();
    $('#property_details_section').hide();
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
    $('#property_details_section').hide();
    $("#schema_property_section").show();
}

function showAllClasses() {
    $("#home-section").hide();
    $("#schema_property_section").hide();
    $('#class_property_section').hide();
    $('#property_details_section').hide();
    $("#schema_class_section").show();
    $("#footer").show();
}

function getSchemaClass(__data) {
    return (
        `
    <tr>           
            <th class="prop-nam" scope="row">
                              <a href="#` + __data[i]["rdfs:label"] +`" onclick="getClass_property('`+__data[i]["rdfs:label"] +`','` + __data[i]["rdfs:comment"] +`');">`+ __data[i]["rdfs:label"] +`</a>
            </th>
            <td>&nbsp;</td><td class="prop-desc">`+ __data[i]["rdfs:comment"] + `</td></tr><tr>
    </tr">
    `
    );
}

function getSchemaProperties(__data) {
    return (
        `
        <tr>           
        <th class="prop-nam" scope="row">
    
    <a href="#` + __data[i]["rdfs:label"] +`" onclick=" getProperty_details('`+__data[i]["rdfs:label"] +`','` + __data[i]["rdfs:comment"] +`');">`+__data[i]['rdfs:label']+`</a>
      </th>
    
    <td class="prop-desc">`+__data[i]['rdfs:comment']+`</td></tr><tr>
      
    
    </tr>
         `
    );
}


function getProperty_details(prop_name,prop_desc) {
    $("#schema_class_section").hide();
    $('#class_property_section').hide();
    $("#schema_property_section").hide();
    $('#property_details_section').show();
    $('#property_details').html("");

    $.ajax({
        url: "/" + prop_name,
        type: "GET",
        contentType: "application/json+ld",
        success: function (data) {
            console.log(data["@graph"]);
            var redirectURL = location.origin + "/" + prop_name;
            var dataResult_graph = data["@graph"];
                          
                $("#property_details").append(
                                `
                <h1>`+dataResult_graph[0]["rdfs:label"]+`</h1">

                <h4><span class="breadcrumbs">`+(dataResult_graph[0]["@type"][0].split("rdf:"))[1]+` &#47; `+(dataResult_graph[0]["@type"][1]).split("iudx:")[1]+`</span>
                </h4>
                
                <div>`+dataResult_graph[0]["rdfs:comment"]+`</div>
                
                <table class="definition-table">
                <thead>
                  <tr>
                    <th>Expected Type Value</th>
                  </tr>
                </thead>
                
                  <tbody><tr>
                  `+ getRangeIncludes(dataResult_graph[0]["iudx:rangeIncludes"]) + `
                    
                  </tr>
                </tbody></table>
                
                <table class="definition-table">
                  <thead>
                    <tr>
                      <th>Used on these types</th>
                    </tr>
                </thead>
                <tbody>
                `+ getDomainIncludes(dataResult_graph[0]["iudx:domainIncludes"]) + `
                </tbody></table> 
                                      
                              `
                            );
        },
        error: function (error) { 
            console.log("Failed");
        }
    });
}

function getClass_property(className, class_desc) {
    // var schema = "Schema:";
    $("#schema_class_section").hide();
    $('#class_property_section').show();
    $('#schema_class').html("");
    $("#class_propertySchema").html("");

    //  document.location.href = 'https://localhost.iudx.org.in:8443/schema_property';
    $.ajax({
        url: "/" + className,
        type: "GET",
        contentType: "application/json+ld",
        success: function (data) {
            console.log(data["@graph"]);
            // var redirectURL = location.origin + "/" + className;
            var dataResult_graph = data["@graph"];
            var arr = [];
            for (i = 0; i < dataResult_graph.length; i++) {
                if (
                    dataResult_graph[i]["@type"] !== undefined &&
                    dataResult_graph[i]["@type"] === "rdf:Property" ||
                    dataResult_graph[i]["@type"] === "iudx:Property" ||
                    dataResult_graph[i]["@type"] === "iudx:TextProperty" ||
                    dataResult_graph[i]["@type"] === "iudx:GeoProperty" ||
                    dataResult_graph[i]["@type"] === "iudx:TimeProperty" ||
                    dataResult_graph[i]["@type"] === "iudx:QuantitativeProperty" ||
                    dataResult_graph[i]["@type"] === "iudx:StructuredProperty"
                ) 
                    {
                                           
                    for (j = 0; j < dataResult_graph[i]["iudx:domainIncludes"].length; j++) {
                        // console.log("domainIncludes");data
                        if (dataResult_graph[i]["iudx:domainIncludes"][j]["@id"].includes("iudx:" + className)) {

                            $("#class_label").text(className);
                            $("#class_desc").text(class_desc);
                            $("#class_propertySchema").append(`
                                
                                <tr>           
                                    <th class="prop-nam" scope="row"><a href="#` +dataResult_graph[i]["rdfs:label"] +`" onclick=" getProperty_details('`+dataResult_graph[i]["rdfs:label"] +`','` + dataResult_graph[i]["rdfs:comment"] +`');">` +
                                dataResult_graph[i]["rdfs:label"] +
                                ` </a>
                                    </th>dataResult_graph
                                   `+ getRangeIncludes(dataResult_graph[i]["iudx:rangeIncludes"]) + `
                                    <td class="prop-desc">` +
                                dataResult_graph[i]["rdfs:comment"] +
                                `</td></tr><tr>
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

function getRangeIncludes(_data) {
    console.log(_data)
    //  for(i=0;i<_data.length;i++){
    //      console.log(_data[i]['@id'])
    //  }
    if (_data.length === 2) {

        return `
    <td class="prop-ect">`+ _data[0]['@id'] + `&nbsp; or <br> ` + _data[1]['@id'] + `&nbsp;</td>
`

    }
    else if (_data.length === 1) {
        return `
    <td class="prop-ect">`+ _data[0]['@id'] + `</td>
`

    }
}

function getDomainIncludes(_data){
    console.log(_data)
    var tableData = [];
    for(i=0;i< _data.length;i++){
        tableData[i] =  `
        <tr>
        <td>`+_data[i]['@id'].split("iudx:")[1]+`</td> 
        </tr>
        `
    }

    return tableData;
}

