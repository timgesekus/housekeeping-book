package net.gesekus.housekeeping.services.eventstore

import org.scalatest.flatspec.AnyFlatSpec
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import java.net.URI
import java.{util => ju}
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import com.amazonaws.services.dynamodbv2.model.KeyType
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType


class DynamoDBSpec extends AnyFlatSpec {
  
  private def createTableRequest(tableName: String, hashKeyName: String) : CreateTableRequest = {
    val attributeDefinitions = new ju.ArrayList[AttributeDefinition]
    attributeDefinitions.add(new AttributeDefinition(hashKeyName, ScalarAttributeType.S))
    val ks = new ju.ArrayList[KeySchemaElement]
    ks.add(new KeySchemaElement(hashKeyName, KeyType.HASH))
    val provisionedthroughput = new ProvisionedThroughput(1000L, 1000L)
    new CreateTableRequest().withTableName(tableName).withAttributeDefinitions(attributeDefinitions).withKeySchema(ks).withProvisionedThroughput(provisionedthroughput)

  }
  
  "A test " should "work" in {
    val client = DynamoDbClient.builder().region(software.amazon.awssdk.regions.Region.EU_WEST_1)
      .endpointOverride(new URI("http://localhost:9000"))                  
      .credentialsProvider(ProfileCredentialsProvider.builder()
                                     .profileName("default")
                                     .build())
                        .build();
    try {
      val request = createTableRequest("BookStore","hallo")
      client.createTable(request)
      val tables = client.listTables()
      Console.err.print (tables)
      /*
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
      */
    } finally client.close()
  }
}
