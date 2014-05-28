SuperStrict

Type t_price
	Field figure:String
	Field stars:Int
End Type

Global price:t_price[50]

RestoreData prices
For Local i:Int = 0 To 49
	price[i] = New t_price
	ReadData price[i].figure
	ReadData price[i].stars
Next

#prices
DefData "25k", 	0
DefData "25k",	0
DefData "30k",	0
DefData "40k",	0
DefData "50k", 	1
DefData "65k", 	1
DefData "75k", 	1
DefData "85k",	2
DefData "100k",	2
DefData "110k",	2
DefData "130k",	2
DefData "150k",	3
DefData "160k",	3
DefData "180k",	3
DefData "200k",	3
DefData "250k",	3
DefData "300k",	4
DefData "350k",	4
DefData "450k",	4
DefData "500k",	4
DefData "550k",	4
DefData "600k",	4
DefData "650k",	5
DefData "700k",	5
DefData "750k",	5
DefData "800k",	5
DefData "850k",	5
DefData "950k",	5
DefData "1M",	5		
DefData "1.1M",	6
DefData "1.3M",	6
DefData "1.5M",	6
DefData "1.6M",	6
DefData "1.8M",	6
DefData "1.9M",	7
DefData "2M",	7
DefData "2.25M",7
DefData "2.75M",7
DefData "3M",	7
DefData "3.5M",	7
DefData "4.5M",	8
DefData "5M",	8
DefData "6M",	8
DefData "7M",	8
DefData "8M",	8
DefData "9M",	8
DefData "10M",	8
DefData "12M",	9
DefData "15M",	9
DefData "15M+",	9
