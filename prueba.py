from  pymongo import MongoClient
import urllib
import pprint

client = MongoClient('localhost', 27017)
db = client['Resto']
posts = db.Resto
vergalisa = posts.find({'name':'Riviera Caterer'})
print(vergalisa)