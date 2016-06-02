## Table of Contents

+ [RUSSEL](#1_0)
+ [Results of the RUSSEL Project Refactoring](#2_0)
    * [Repository Services](#2_0_1)
    * [Metadata Services](#2_0_2)
    * [Paradata](#2_0_3)
+ [Instructions for Setting up RUSSEL](#3_0)
    * [Code projects in this Github repository required to build RUSSEL](#3_0_1)
    * [Prerequisites](#3_0_2)
    * [VMware VM settings](#3_1)
    * [Install Ubuntu OS](#3_2)
    * [Install Eclipse Development Environment](#3_3)
    * [Install Java 7](#3_4)
    * [Install Java JDK](#3_5)
    * [Install Apache Ant/Git](#3_6)
    * [Install Apache Tomcat 7](#3_7)
    * [Install Apache Solr DB](#3_8)
    * [Setup Apache Solr DB for RUSSEL](#3_8_1)
    * [Download the source code from this repository](#3_9)
    * [Setting up and building the russel-ui source project](#3_10)
    * [Setting up the russel-ui and EwGWTLib projects](#3_11)
    * [To build the russel-ui project](#3_11_1)
    * [Building the LEVR components](#3_12)
    * [Installing LEVR scripts](#3_13)
    * [Creating a location for the database files](#3_14)
    * [Install and configure the russel-ui project](#3_15)
    * [Open russel/js/installation.settings for edit](#3_15_1)
    * [Open /var/lib/tomcat7/etc/russel0Settings.rs2 for edit](#3_15_2)
    * [Final Steps](#3_16)
+ [Contributing to the Project](#Contributing-to-the-Project)
+ [License](#License)


# <a name="1_0"></a>RUSSEL

RUSSEL, (*Reusable Support System for E-Learning*) can be thought of as a lightweight open source learning content management system (LCMS). It includes a learning object repository that can be used to organize and tag digital assets for use in online training environments and a content discovery and assembly tool that can be used to add assets to vetted instructional design templates and to pass them on to developers and programmers for use in creating courseware.
RUSSEL is intended for use by Subject Matter Experts (SMEs), Instructional System Designers (ISDs), training managers, training suppliers, and others involved in the creation and management of digital learning objects. It is integrated with the Learning Registry (LR), a national registry of online learning objects that can be accessed at [http://learningregistry.org](http://learningregistry.org "The Learning Registry"), and can be integrated with other registries, learning management systems (LMS), authoring tools, and repositories. With recent improvements made to this project, RUSSEL now includes: 
+ User and group management and tools for creating collections of digital assets
+ Tools for importing and exporting SCORM packages
+ Tools tagging objects with metadata relevant to their use in military and other instruction 
+ Tools for using resources in instructional design templates
+ Automated registration of objects, their metadata, and paradata in the LR. Paradata includes user ratings and comments (supported by RUSSEL) and usage data collected by RUSSEL. 
+ APIs for integration with registries and repositories. 

## <a name="2_0"></a>Results of the RUSSEL Project Refactoring

RUSSEL was originally developed using the open source version of Alfresco as a back-end. The thinking was that an existing enterprise content management system could provide much of the required core functionality, freeing the RUSSEL team to focus on improving the User Interface and User Experience (UI/UX) and developing the specialized components that support asset reuse and instructional design in a DOD setting. Alfresco was selected after an extensive environmental scan and decision process, in part because a commercial (non-open source) version was also available and in DOD use. The use of Alfresco did, in fact, permit more progress to be made on innovative portions of RUSSEL, but several key limitations of Alfresco were exposed in the process. Overcoming these limitations required developing separate Java beans that interacted with Alfresco, and the resulting application still had limited ability to expose functionality through the equivalent of an enterprise service bus (ESB). The re-architecture of RUSSEL (under the DECALS project) using LEVR solved these problems and has made RUSSEL more extensible.

### <a name="2_0_1"></a>Repository Services
RUSSEL offers standard learning object repository services. These include “Create, Retrieve, Update and Delete” (CRUD) services, version control, previewing, and meta-tagging. A tile-based drag-and-drop interface gives RUSSEL a different look and feel. The important aspects of RUSSEL as a repository are:
+ It is open source;
+ Other systems, including PALs, can access their functionality through RESTFUL web services; and
+ It contains tagging features that specifically support DOD education and training applications.

### <a name="2_0_2"></a>Metadata Services
In RUSSEL, when you add an object, you are automatically taken to a screen that gives you the opportunity to edit its metadata. Some fields are pre-populated with automatically extracted metadata, and both systems are designed to integrate with third party systems that can automatically generate more fields. The existing keyword extraction uses Eduworks’ proprietary keyword generation service that is licensed to the ADL for use in PAL projects together with other semantic services. RUSSEL extracts resource types, size and duration (keyed to learners at a high school reading level), and auto-generate descriptions of resources from other extracted metadata fields. These descriptions are not high quality but serve as a starting point that can be edited, an approach that is called semi-automated metadata generation in the literature.
 
The RUSSEL project has emphasized automated and semi-automated extraction of metadata over purely manual entry. There are two main reasons for this. First, Authors and instructors tend not to add metadata to resources when they create them or add them to repositories. Users generally dislike filling out long forms when there is no clear personal benefit, and requiring extensive metadata serves as a barrier to submission. Second, automated methods can be more consistent than manual ones, especially with fields such as size or reading level that can be calculated directly or using a combination of formulaic and machine learning methods.
 
### <a name="2_0_3"></a>Paradata
Starting about ten years ago, the notion of paradata started creeping into the vocabulary used by digital librarians and the LR. Originally used to describe survey data, paradata is now used to describe “social metadata” ranging from ratings and comments to usage statistics. In simple terms, 
+ Metadata makes assertions about an object.
+ Paradata makes assertions about the use of an object.

RUSSEL collects paradata in the form of ratings, comments and usage statistics. This system allows users to rate and comment on resources. RUSSEL tracks where resources are used in instructional design templates and uses this as part of its internal search criteria. It also tracks how often resources are added to collections or projects.
 
This paradata is maintained internally and can be manually published to the LR. It is not automatically published because not all users may wish paradata for all objects to be stored in the LR and because pushing it to the LR on every update would use a lot of server bandwidth as currently implemented.

## <a name="3_0"></a>Instructions for Setting up RUSSEL

These are instructions for getting the RUSSEL System installed and running on a VMware VM running Ubuntu Linux version 14.04.3. These instructions can also be used to setup the system software on a server running Ubuntu Linux version 14.04.3.

#### <a name="3_0_1"></a>Code projects in this Github repository required to build RUSSEL:
+ russel-ui
+ [EwGWTLib](https://github.com/adlnet/DECALS/tree/master/EwGWTLib "EwGWTLib project")
+ [LEVR](https://github.com/adlnet/DECALS/tree/master/LEVR "LEVR source")
    * [eduworks-common](https://github.com/adlnet/DECALS/tree/master/LEVR/eduworks-common "eduworks-common")
    * [levr-core](https://github.com/adlnet/DECALS/tree/master/LEVR/levr-core "levr-core")
    * [levr-base](https://github.com/adlnet/DECALS/tree/master/LEVR/levr-base "levr-base")
+ scripts
    * [base-v2](https://github.com/adlnet/DECALS/tree/master/scripts/base-v2)
    * [base](https://github.com/adlnet/DECALS/tree/master/scripts/base)
    * russel


#### <a name="3_0_2"></a>Prerequisites:
+ VMWare Player 7.1.2 or higher OR
+ A web server running Linux
+ Ubuntu Linux 14.04.3 64 bit
+ Java 7
+ Tomcat 7
+ Solr
+ Eclipse Kepler v4.3 for Java EE Developers 64-bit and the GWT plugin


[Download and Install VMware Player 7.1.2](https://my.vmware.com/web/vmware/free#desktop_end_user_computing/vmware_player/7_0 "VMware Player 7.1.1")

#### <a name="3_1"></a>VMware VM settings:
+ CPUs: 2
+ RAM: 4GB
+ Vitualization Engine: Intel VT-x/EPT or AMD V/RVI with Virtualize Intel VT-x/EPT or AMD V/RVI box checked
+ VM OS: Ubuntu 14.04.3


### <a name="3_2"></a>Install Ubuntu OS
[Download Ubuntu 14.04.3 and install in the new VM](http://www.ubuntu.com/download/desktop "Ubuntu 14.04.3")

[Ubuntu 14.04.3 LTS 64-bit installation in VMware player 2015](https://www.youtube.com/watch?v=PZFyhzUcwjA "YouTube how-to video")

### <a name="3_3"></a>Install Eclipse Development Environment

[Download and install Eclipse Kepler v4.3 for Java EE Developers 64-bit on VM](http://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/kepler/SR2/eclipse-jee-kepler-SR2-linux-gtk-x86_64.tar.gz "Eclipse Kepler v4.3 for Java EE Developers")

[Install GWT Eclipse Plugin](https://developers.google.com/eclipse/docs/install-eclipse-4.3 "GWT Eclipse Plugin")

### <a name="3_4"></a>Install Java 7:
```
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java7-installer
sudo apt-get install oracle-java7-set-default
```

### <a name="3_5"></a>Install Java JDK:
```
sudo apt-get install default-jdk
```

### <a name="3_6"></a>Install Apache Ant/Git:
```
sudo apt-get install ant git
```

### <a name="3_7"></a>Install Apache Tomcat 7:
```
sudo apt-get install tomcat7
```

### <a name="3_8"></a>Install Apache Solr DB:

[Download Solr](http://mirror.metrocast.net/apache/lucene/solr/4.10.0 "Solr 4.10.0")

**Untar file:**
```
tar -zxvf solr-4.10.0.tgz
```

**Copy contents of folder /solr-4.10.0/example/lib/ext/ to /usr/share/tomcat7/lib/**
```
cp -r ~/solr-4.10.0/example/lib/ext/*.jar /usr/share/tomcat7/lib/
```

**Edit file "tomcat-users.xml" in directory /var/lib/tomcat7/conf/ and setup these users:**
```xml
<tomcat-users>
  <role rolename="manager"/>
  <role rolename="manager-gui"/>
  <role rolename="admin"/>
  <role rolename="admin-gui"/>
  <user username="tomcat" password="tomcat" roles="manager,manager-gui, admin, admin-gui"/>
</tomcat-users> 
```

#### <a name="3_8_1"></a>Setup Apache Solr DB for RUSSEL:


**Copy contents of folder /solr-4.10.0/example/solr/ to /var/lib/tomcat7/russelSolr**

```
sudo mkdir /var/lib/tomcat7/russelSolr/
cp -r ~/solr-4.x.x/example/solr/ /var/lib/tomcat7/russelSolr/ 
```

**Copy /solr-4.10.0/dist/solr-4.10.0.war to /var/lib/tomcat7/russelSolr/russelSolr.war**
```
cp ~/solr-4.10.0/dist/solr-4.10.0.war /var/lib/tomcat7/russelSolr/russelSolr.war
```

**set permissions on russelSolr folder:**
```
sudo chmod -R 755 /var/lib/tomcat7/russelSolr/
```

**Create file "russelSolr.xml" in directory /var/lib/tomcat7/conf/Catalina/localhost/ with the following contents:**
```
cd /var/lib/tomcat7/conf/Catalina/localhost/
sudo vim russelSolr.xml
```
```xml
<?xml version="1.0" encoding="utf-8"?>
<Context docBase="/var/lib/tomcat7/russelSolr/russelSolr.war" debug="0" crossContext="true">
<Environment name="solr/home" type="java.lang.String" value="/var/lib/tomcat7/russelSolr" override="true" />
</Context>
```

### <a name="3_9"></a>Download the source code from this repository:

Create a “Source/” directory to hold the master repositories from Github and create a “Development/” directory to build the code.
```
sudo mkdir Source
sudo mkdir Development
```

**To download the RUSSEL repositories from Github:**

```
cd Source
sudo git clone https://github.com/adlnet/RUSSEL.git  
```

**To download the RUSSEL support project repositories from Github (located in the DECALS repository):**

```
cd Source
sudo git init
sudo git config core.sparseCheckout true
sudo vim .git/info/sparse-checkout
```
Add the following to the file sparse-checkout:
```
EwGWTLib/
LEVR/
scripts/base-v2/
scripts/base/
```
save the file.

```
sudo git remote add -f origin https://github.com/adlnet/DECALS.git
sudo git checkout master
```

**Once the files have finished downloading copy them over to your Development/ folder:**
```
sudo cp -R * ~/Development/
```

### <a name="3_10"></a>Setting up and building the russel-ui source project

### <a name="3_11"></a>Setting up the russel-ui and EwGWTLib projects

Run the Eclipse IDE
Once Eclipse has finished loading, 
+ go to  **File->Import**
+ From the Import dialog, select **General->Existing Projects into Workspace** and click Next>
+ Browse to ~/Development/decals-ui/ and click Finish

This should place the project russel-ui in the Project Explorer.
Next, from the Import dialog, 
+ select **General->Existing Projects into Workspace** and click Next>
+ Browse to ~/Development/EwGWTLib/ and click Finish

This should place the project EwGWTLib in the Project Explorer.

+ Right-click on the russel-ui project and select properties
+ From the properties dialog select Java Build Path.
+ Under the Libraries tab, make sure the GWT SDK is set. If not, click edit and select Use specific SDK: GWT 2.6.0 or higher.
+ Right-click on the EwGWTLib project and select properties
+ From the properties dialog select Java Build Path.
+ Under the Libraries tab, make sure the GWT SDK is set. If not, click edit and select Use specific SDK: GWT 2.6.0 or higher.
+ If the GWT SDK library is not in the list, make sure that the following jar files point to plugins\com.google.gwt.eclipse.sdkbundle_2.6.0\gwt-2.6.0:
⋅⋅* gwt-dev.jar
⋅⋅* gwt-user.jar
⋅⋅* validation-api-1.0.0.GA.jar
⋅⋅* validation-api-1.0.0.GA-sources.jar

#### <a name="3_11_1"></a>To build the russel-ui project:
+ Right-click on the russel-ui project and select Google->GWT Compile
+ From the dialog choose the output style of choice and click compile.


### <a name="3_12"></a>Building the LEVR components (levr.war file)

**Some changes need to be made in the original build.xml files:**

+ in build.xml for eduworks-common, change **"eduworks-common-usage"** on top line to **"eduworks-common-jar"**
+ in build.xml for levr-core change **"levr-core-dist"** on top line to **"levr-core-jar"**

The projects: eduworks-common, levr-core and levr-base must be built in the following order:
1. eduworks-common
2. levr-core
3. levr-base

**To build each project, from the Linux commandline run:**
```
cd ~Development/LEVR/eduworks-common/
sudo ant 
cd ~Development/LEVR/levr-core/
sudo ant 
cd ~Development/LEVR/levr-base/
sudo ant
```

Once each project has been built, copy the file, **levr.war**, from the Development/LEVR/levr-base/dist folder to /var/lib/tomcat7/webapps/ directory
```
sudo cp levr.war /var/lib/tomcat7/webapps/
```

**NOTE**: After the initial LEVR build and before copying any newly built levr.war file over, it is a good idea to stop the tomcat7 service and remove the old levr.war file and associated levr directory from the /var/lib/tomcat7/webapps/ directory:
```
sudo service tomcat7 stop (wait to start service again once all projects are setup and configured – see below)
cd /var/lib/tomcat7/webapps/
sudo rm –R lev*
```

### <a name="3_13"></a>Installing LEVR scripts

**Create a directory, etc/ under /var/lib/tomcat7/:**
```
sudo mkdir etc/
```

**Copy *.rs2 script files from ~/Source/scripts/base-v2/, ~/Source/scripts/base/, and ~/Source/scripts/russel/ to /var/lib/tomcat7/etc directory:**

```
cd ~/Source/scripts/base-v2/
sudo cp * /var/lib/tomcat7/etc/

cd ~/Source/scripts/base/
sudo cp * /var/lib/tomcat7/etc/

cd ~/Source/scripts/russel/
sudo cp * /var/lib/tomcat7/etc/
```
**make the owner of etc/ and all sub files and folders tomcat7:**
```
sudo chown –R tomcat7:root etc/
```

### <a name="3_14"></a>Creating a location for the database files

**Create a directory, db/ under /var/lib/tomcat7/:**
```
sudo mkdir db/
```
**make the owner of db/ and all sub files and folders tomcat7:**
```
sudo chown –R tomcat7:root db/
```

### <a name="3_15"></a>Install and configure the russel-ui project

**Create a russel/ directory under webapps/ROOT/:**
```
sudo mkdir /var/lib/tomcat7/webapps/ROOT/russel/
```
**Copy the contents of the ~/Development/russel-ui/war/ to /var/lib/tomcat7/webapps/ROOT/russel/:**
```
cd ~/Development/russel-ui/war/
sudo cp –R * /var/lib/tomcat7/webapps/ROOT/russel/
```

#### <a name="3_15_1"></a>Open russel/js/installation.settings for edit:

```
cd /var/lib/tomcat7/webapps/ROOT/russel/js/
sudo vim installation.settings
```
**File contents should be:**
```
site.name="RUSSEL"
root.url="http://<server url>/"
esb.url="http://<server url>/levr/api/custom/"
alfresco.url="N/A"
site.url="http://<server url>/russel/"
help.url="N/A"
feedback.email="N/A"
```

save the file.

#### <a name="3_15_2"></a>Open /var/lib/tomcat7/etc/russel0Settings.rs2 for edit:
The following lines should be edited for your local server environment:

line 1:
```
adminUsername = #string(obj="<enter admin username>");
```

line 4:
```
adminPassword = #string(obj="<enter admin password>");
```

line 13:
```
urlMetadataGeneration = #string(obj="http://service.metaglance.com/metadataLite/russel/generate");
```

line 16 (the location is URL encoded):
```
urlSolrSearch = #string(obj="http%3A%2F%2<server url>%2FrusselSolr%2F");
```

save the file.

### <a name="3_16"></a>Final Steps

Make sure the following files, directories and subdirectories are set to ownership tomcat7:root:
+ /var/lib/tomcat7/etc/
+ /var/lib/tomcat7/db/
+ /var/lib/tomcat7/russelSolr/
+ /var/lib/tomcat7/webapps/levr.war
+ /var/lib/tomcat7/webapps/ROOT/russel/
```
sudo chown -R tomcat7:root <file or directory>
```
To start the tomcat7 service:
```
sudo service tomcat7 start
```

Navigate to ```http://<server url>/russel/``` to visit RUSSEL website
Navigate to ```http://<server url>/russelSolr/``` to check russelSolr DB stats

## Contributing to the project <a name="Contributing-to-the-Project"></a>
We welcome contributions to this project. Fork this repository, make changes, and submit pull requests. If you're not comfortable with editing the code, please [submit an issue](https://github.com/adlnet/RUSSEL/issues) and we'll be happy to address it. 

## License <a name="License"></a>
   Copyright &copy;2016 Advanced Distributed Learning

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.


