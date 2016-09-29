## Watch Over Me - Crime Data Engine Project (Safer Streets)

### Setup Build Environment

Each environment may have different setup (for example database connection and Elastic Beanstalk environment).

To allow multiple environment in this project, please duplicate `/local.properties` and rename it to your desired environment.

To build, please run `ant` with an argument `-Dbuild.env={your-environment-name}`. If you don't provide the argument, Ant will use `local` environment.

E.g.:

    ant clean war                         ## local env
    ant clean war -Dbuild.env=production  ## production env

FYI: All `/conf/**/*.xml` files will be process in Ant Replace Token filter. So, any string within `@` and `@` will be replaced by corresponding properties in `/${build.env}.properties`.


## Initial Configuration
You will need to edit two files, `local.properties` and  `conf/SystemConfig.properties` first and add in your relevant settings and keys.


### Deployment to AWS Elastic Beanstalk

#### Requirements

We need this app to deploy automatically to AWS Elastic Beanstalk:

* [AWS CLI (aws)](http://aws.amazon.com/cli/)
* [AWS Elastic Beanstalk CLI (eb)](http://docs.aws.amazon.com/elasticbeanstalk/latest/dg/eb-cli3.html)

Install:

	sudo apt-get install python-dev
	sudo pip install awsebcli
	sudo pip install awscli

AWS CLI configuration (first time):

	aws configure

#### How to Deploy

1. Clean project, create WAR and deploy it to AWS Elastic Beanstalk.

		ant clean aws-eb-deploy -Dbuild.env=test

1. Turn off NAT service (optional, if not being required anymore).

		ant aws-vpc-nat-stop -Dbuild.env=test
