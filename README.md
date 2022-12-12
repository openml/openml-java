[![License](https://img.shields.io/badge/License-BSD%203--Clause-blue.svg)](https://opensource.org/licenses/BSD-3-Clause)

# java
Java library to interface with OpenML

The Java App is used for a number of OpenML components, such as the ARFF parser and Evaluation engine, which depend on the Weka API. It is invoked from the OpenML API by means of a CLI interface. Typically, a call looks like this:

`java -jar webapplication.jar -config "api_key=S3CR3T_AP1_K3Y" -f evaluate_run -r 500`

Which in this case executes the webapplication jar, invokes the function "evaluate run" and gives it parameter run id 500. The config parameter can be used to set some config items, in this case the api_key is mandatory. Every OpenML user has an api_key, which can be downloaded from their [OpenML profile page](http://www.openml.org/u). The response of this function is a call to the OpenML API uploading evaluation results to the OpenML database. Note that in this case the PHP website invokes the Java webapplication, which makes a call to the PHP website again, albeit another endpoint. 

The webapplication does not have direct writing rights into the database. All communication to the database goes by means of the [OpenML Connector](http://search.maven.org/#search|ga|1|g%3A%22org.openml%22), which communicates with the OpenML API. As a consequence, the webapplication could run on any system, i.e., there is no formal need for the webapplication to be on the same server as the website code. This is important, since this created modularity, and not all servers provide a command line interface to PHP scripts.

Another example is the following:

`java -jar webapplication -config "api_key=S3CR3T_AP1_K3Y" -f all_wrong -r 81,161 -t 59`

Which takes a comma separated list of run ids (no spaces) and a task id as input and outputs the test examples on the dataset on which all algorithms used in the runs produced wrong examples (in this case, weka.BayesNet_K2 and weka.SMO, respectively). An error will be displayed if there are runs not consistent with the task id in there. 

## Extending the Java App

The bootstrap class of the webapplication is

`org.openml.webapplication.Main`

It automatically checks authentication settings (such as api_key) and the determines which function to invoke. 

It uses a switch-like if - else contruction to facilitate the functionalities of the various functions. Additional functions can be added to this freely. From there on, it is easy to add functionality to the webapplication. 

Parameters are handled using the Apache Commons CommandLineParser class, which makes sure that the passed parameters are available to the program. 

In order to make new functionalities available to the website, there also needs to be programmed an interface to the function, somewhere in the website. The next section details on that. 

## Interfacing from the OpenML API
By design, the REST API is not allowed to communicate with the Java App. All interfaces with the Java webapplication should go through other controllers of the PHP CodeIgniter framework., for example api_splits. Currently, the website features two main API's. These are represented by a Controller. Controllers can be found in the folder openml_OS/controllers. Here we see:
* api_new.php, representing the REST API
* api_splits.php, representing an API interfacing to the Java webapplication. 

# Guide

The Java API allows you connect to OpenML from Java applications.

## Java Docs

[Read the full Java Docs](https://openml.github.io/openml-java/).

## Download
Stable releases of the Java API are available from [Maven Central](https://search.maven.org/search?q=a:apiconnector)
Or, you can check out the developer version from [GitHub](https://github.com/openml/java)

Include the jar file in your projects as usual, or [install via Maven](http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html).

## Quick Start
* Create an <code>OpenmlConnector</code> instance with your authentication details. This will create a client with all OpenML functionalities.
> OpenmlConnector client = new OpenmlConnector("api_key")

All functions are described in the [Java Docs](https://www.openml.org/docs).

### Downloading
To download data, flows, tasks, runs, etc. you need the unique id of that resource. The id is shown on each item's webpage and in the corresponding url. For instance, let's download [Data set 1](d/1). The following returns a DataSetDescription object that contains all information about that data set.

```
DataSetDescription data = client.dataGet(1);
```

You can also [search](search) for the items you need online, and click the icon to get all id's that match a search.

### Uploading

To upload data, flows, runs, etc. you need to provide a description of the object. We provide wrapper classes to provide this information, e.g. `DataSetDescription`, as well as to capture the server response, e.g. `UploadDataSet`, which always includes the generated id for reference:

```
DataSetDescription description = new DataSetDescription( "iris", "The famous iris dataset", "arff", "class");
UploadDataSet result = client.dataUpload( description, datasetFile );
int data_id = result.getId();
```

More details are given in the corresponding functions below. Also see the [Java Docs](docs) for all possible inputs and return values.

### Data download

#### `dataGet(int data_id)`

Retrieves the description of a specified data set.

```
DataSetDescription data = client.dataGet(1);
String name = data.getName();
String version = data.getVersion();
String description = data.getDescription();
String url = data.getUrl();
```

#### `dataFeatures(int data_id)`

Retrieves the description of the features of a specified data set.

```
DataFeature reponse = client.dataFeatures(1);
DataFeature.Feature[] features = reponse.getFeatures();
String name = features[0].getName();
String type = features[0].getDataType();
boolean	isTarget = features[0].getIs_target();
```

#### `dataQuality(int data_id)`

Retrieves the description of the qualities (meta-features) of a specified data set.

```
    DataQuality response = client.dataQuality(1);
    DataQuality.Quality[] qualities = reponse.getQualities();
    String name = qualities[0].getName();
    String value = qualities[0].getValue();
```

#### `dataQuality(int data_id, int start, int end, int interval_size)`

For data streams. Retrieves the description of the qualities (meta-features) of a specified portion of a data stream.

```
    DataQuality qualities = client.dataQuality(1,0,10000,null);
```

#### `dataQualityList()`

Retrieves a list of all data qualities known to OpenML.

```
    DataQualityList response = client.dataQualityList();
    String[] qualities = response.getQualities();
```

### Data upload

#### `dataUpload(DataSetDescription description, File dataset)`

Uploads a data set file to OpenML given a description. Throws an exception if the upload failed, see [openml.data.upload](#openml_data_upload) for error codes.

```
    DataSetDescription dataset = new DataSetDescription( "iris", "The iris dataset", "arff", "class");
    UploadDataSet data = client.dataUpload( dataset, new File("data/path"));
    int data_id = result.getId();
```


#### `dataUpload(DataSetDescription description)`

Registers an existing dataset (hosted elsewhere). The description needs to include the url of the data set. Throws an exception if the upload failed, see [openml.data.upload](#openml_data_upload) for error codes.

```
    DataSetDescription description = new DataSetDescription( "iris", "The iris dataset", "arff", "class");
    description.setUrl("http://datarepository.org/mydataset");
    UploadDataSet data = client.dataUpload( description );
    int data_id = result.getId();
```

### Flow download

#### `flowGet(int flow_id)`

Retrieves the description of the flow/implementation with the given id.

```
    Implementation flow = client.flowGet(100);
    String name = flow.getName();
    String version = flow.getVersion();
    String description = flow.getDescription();
    String binary_url = flow.getBinary_url();
    String source_url = flow.getSource_url();
    Parameter[] parameters = flow.getParameter();
```

### Flow management

#### `flowOwned()`

Retrieves an array of id's of all flows/implementations owned by you.

```
    ImplementationOwned response = client.flowOwned();
    Integer[] ids = response.getIds();
```

#### `flowExists(String name, String version)`

Checks whether an implementation with the given name and version is already registered on OpenML.

```
    ImplementationExists check = client.flowExists("weka.j48", "3.7.12");
    boolean exists = check.exists();
    int flow_id = check.getId();
```

#### `flowDelete(int id)`

Removes the flow with the given id (if you are its owner).

```
    ImplementationDelete response = client.openmlImplementationDelete(100);
```

### Flow upload

#### `flowUpload(Implementation description, File binary, File source)`

Uploads implementation files (binary and/or source) to OpenML given a description.

```
    Implementation flow = new Implementation("weka.J48", "3.7.12", "description", "Java", "WEKA 3.7.12")
    UploadImplementation response = client.flowUpload( flow, new File("code.jar"), new File("source.zip"));
    int flow_id = response.getId();
```

### Task download

#### `taskGet(int task_id)`

Retrieves the description of the task with the given id.

```
    Task task = client.taskGet(1);
    String task_type = task.getTask_type();
    Input[] inputs = task.getInputs();
    Output[] outputs = task.getOutputs();
```

#### `taskEvaluations(int task_id)`

Retrieves all evaluations for the task with the given id.

```
    TaskEvaluations response = client.taskEvaluations(1);
    Evaluation[] evaluations = response.getEvaluation();
```

#### `taskEvaluations(int task_id, int start, int end, int interval_size)`

For data streams. Retrieves all evaluations for the task over the specified window of the stream.

```
    TaskEvaluations response = client.taskEvaluations(1);
    Evaluation[] evaluations = response.getEvaluation();
```

### Run download

#### `runGet(int run_id)`

Retrieves the description of the run with the given id.

```
    Run run = client.runGet(1);
    int task_id = run.getTask_id();
    int flow_id = run.getImplementation_id();
    Parameter_setting[] settings = run.getParameter_settings()
    EvaluationScore[] scores = run.getOutputEvaluation();
```

### Run management

#### `runDelete(int run_id)`

Deletes the run with the given id (if you are its owner).

```
    RunDelete response = client.runDelete(1);
```

### Run upload

#### `runUpload(Run description, Map<String,File> output_files)`

Uploads a run to OpenML, including a description and a set of output files depending on the task type.

```
    Run.Parameter_setting[] parameter_settings = new Run.Parameter_setting[1];
    parameter_settings[0] = Run.Parameter_setting(null, "M", "2");
    Run run = new Run("1", null, "100", "setup_string", parameter_settings);
    Map outputs = new HashMap<String,File>();
    outputs.add("predictions",new File("predictions.arff"));
    UploadRun response = client.runUpload( run, outputs);
    int run_id = response.getRun_id();
```
