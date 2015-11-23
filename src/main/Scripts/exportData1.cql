COPY getthelead.TweetsTest 
(Id, UserId,User, CurrentUserRetweetId, 
InReplyToUserId,ProfileLocation,Message,CreatedAt,IsRetweet,IsRetweeted,
IsFavorited,QuotedStatusId,Contributors,InReplyToScreenName,RetweetCount,
GeoLocationLatitude,GeoLocationLongitude,HashtagEntities,PlaceCountry,
PlaceFullName,Source,IsPossiblySensitive, IsTruncated)
TO '/home/mary/Documents/Tesis/TweetsTestTemp.csv';
