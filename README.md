# Newsroom

Newsroom is a news aggregation tool built using Play Java and compiled using sbt.

## Prerequisites

This project uses Java 15 and sbt 1.3.13 - in order to ensure complete functionality, make sure your system uses the same versions. 
The project runs on ```localhost:9000```

In addition to this, the project uses YoutubeDL to handle subtitles. YoutubeDL cannot simply be used with a jar it also needs the executable path set to where the downloaded ```youtube-dl``` executable is - the path is set on line 143 of ```SearchYoutube.java```. YoutubeDL can be installed using Homebrew on Mac, other download instructions can be found [here](https://youtube-dl.org/).

## Directory updating
Due to the way files are read in, in this system, they cannot be written dynamically therefore the paths on lines 
67, 104, 142 and 164 in ```SearchYoutube.java``` should be updated to reflect the directory where newsroom is kept.

## Usage

```bash
# to install packages and compile
sbt compile

# to run
sbt run
```


## Populating the ElasticSearch instance

To search Elasticsearch you must have an instance of Elasticsearch running on Docker. The version this system used was 7.16.2.
To download this file run the command below, the start the instance using Docker.
```bash
docker pull docker.elastic.co/elasticsearch/elasticsearch:7.16.2
```
Before you populate the Elasticsearch instance you must first update the path on line 32 of ```ArticleController.java``` to reflect where the Newsroom system is stored on your machine.
Then, run the application and navigate to ```localhost:9000/articles/index``` to populate the database. Once this is done the webpage will tell you and you can return to the homepage on ```localhost:9000```
and begin using the system.

