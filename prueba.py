from  pymongo import MongoClient
import urllib
import pprint

client = MongoClient('localhost', 27017)
db = client['Barrios']
posts = db.Barrios
query = {'name':'Avellaneda'}
l = posts.find(query)
j = {}
for v in l:
    j = v
print(j)