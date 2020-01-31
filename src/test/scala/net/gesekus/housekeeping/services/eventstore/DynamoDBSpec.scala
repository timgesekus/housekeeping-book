package net.gesekus.housekeeping.services.eventstore

import com.amazonaws.services.dynamodbv2.model.CreateTableRequest
import org.scalatest.FlatSpec
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import com.amazonaws.services.dynamodbv2.model.KeyType
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType
import java.util

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDBAsync, AmazonDynamoDBAsyncClient}
import com.gu.scanamo.LocalDynamoDB
import com.gu.scanamo.syntax._


class DynamoDBSpec extends FlatSpec {

  import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
  import com.amazonaws.services.dynamodbv2.model.AttributeDefinition
  import com.amazonaws.services.dynamodbv2.model.CreateTableRequest
  import com.amazonaws.services.dynamodbv2.model.CreateTableResult
  import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
  import com.amazonaws.services.dynamodbv2.model.KeyType
  import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
  import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType
  import java.util

  private def createTable(ddb: AmazonDynamoDB, tableName: String, hashKeyName: String) = {
    val attributeDefinitions = new util.ArrayList[AttributeDefinition]
    attributeDefinitions.add(new AttributeDefinition(hashKeyName, ScalarAttributeType.S))
    val ks = new util.ArrayList[KeySchemaElement]
    ks.add(new KeySchemaElement(hashKeyName, KeyType.HASH))
    val provisionedthroughput = new ProvisionedThroughput(1000L, 1000L)
    val request = new CreateTableRequest().withTableName(tableName).withAttributeDefinitions(attributeDefinitions).withKeySchema(ks).withProvisionedThroughput(provisionedthroughput)
    ddb.createTable(request)
  }
  "A test " should "work" in {
    import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
    import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded

    import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
    import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded
    import com.amazonaws.services.dynamodbv2.model.CreateTableResult
    import com.amazonaws.services.dynamodbv2.model.ListTablesResult
    import com.amazonaws.services.dynamodbv2.model.TableDescription
    val ddb = DynamoDBEmbedded.create.amazonDynamoDB
    try {
      val tableName = "Movies"
      val hashKeyName = "film_id"
      val res = createTable(ddb, tableName, hashKeyName)
      val tableDesc = res.getTableDescription
      assert (tableName == tableDesc.getTableName)
      assert("[{AttributeName: " + hashKeyName + ",KeyType: HASH}]" == tableDesc.getKeySchema.toString)
      assert("[{AttributeName: " + hashKeyName + ",AttributeType: S}]" == tableDesc.getAttributeDefinitions.toString)
      assert(1000L == tableDesc.getProvisionedThroughput.getReadCapacityUnits)
      assert(1000L == tableDesc.getProvisionedThroughput.getWriteCapacityUnits)
      assert("ACTIVE" == tableDesc.getTableStatus)
      assert("arn:aws:dynamodb:ddblocal:000000000000:table/Movies" == tableDesc.getTableArn)
      val tables = ddb.listTables
      assert(2 == tables.getTableNames.size)
    } finally ddb.shutdown()
  }
}
