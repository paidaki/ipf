/*
 * Copyright 2009 the original author or authors.
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
package builders.content.section

import org.openhealthtools.ihe.common.cdar2.*

// Chapter 3.14: Procedures
// Depends on  : ccd_procedureActivity

// CONF-422: CCD SHOULD contain exactly one and SHALL NOT contain more than one 
//           Procedures section (templateId 2.16.840.1.113883.10.20.1.12). 
//           The Procedures section SHALL contain a narrative block, and SHOULD 
//           contain clinical statements. Clinical statements SHOULD include one 
//           or more procedure activities (templateId 2.16.840.1.113883.10.20.1.29).
ccd_procedures(schema:'ccd_section'){
    properties{
        // CONF-423: The procedure section SHALL contain Section / code.
        // CONF-424: The value for �Section / code� SHALL be �47519-4� �History of procedures�
        //           2.16.840.1.113883.6.1 LOINC STATIC.
        code(schema:'loincCode', def: {
           getMetaBuilder().build {
               loincCode(code:'47519-4', displayName:'History of procedures')
           }
       })
       // CONF-425: The procedure section SHALL contain Section / title.
       // CONF-426: Section / title SHOULD be valued with a case-insensitive 
       //           language-insensitive text string containing �procedures�.
       title(check: { it.text =~ /(?i)procedures/ }, def: {
           getMetaBuilder().build {
               st('Procedures')
           }
       })
       text(schema:'strucDocText', req:true)
       procedureActivity(schema:'ccd_procedureActivity')
    }
    collections{
        templateIds(collection:'templateId', def: {
            getMetaBuilder().buildList {
              ii(root:'2.16.840.1.113883.10.20.1.12')
            }
        })
    }    
}