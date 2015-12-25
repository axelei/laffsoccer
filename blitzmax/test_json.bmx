SuperStrict

Import "t_json.bmx"

'JSON.print_types()

'JSON.debug = True

Local out:String, check:String



''TEST1: null

out = JSON.stringify(Null)

check = "null"

If (out = check)
	Print "TEST1 OK"
Else
	Print "TEST1 KO"
	Print out
	Print check
EndIf



''TEST2: void object

Type t0
End Type

Local i0:t0 = New t0

out = JSON.stringify(i0)

check = "{}"

If (out = check)
	Print "TEST2 OK"
Else
	Print "TEST2 KO"
	Print out
	Print check
EndIf



''TEST3: object, no separator
Type t1
	Field f1:Byte
	Field f2:Short
	Field f3:Int
	Field f4:Long
	Field f5:Float
	Field f6:Double
	Field f7:String
	Field f8:Object
	Field f9:TList
	Field f10:TMap
	Field f11:TList
	Field f12:TMap
	
	Method New()
		Self.f1 = 255
		Self.f2 = 65535
		Self.f3 = +2^31-1
		Self.f4 = 2^63-1
		Self.f5 = 10^38
		Self.f6 = 10^308
		Self.f7 = "Fær Øer"
		Local i:st = New st
		i.f = 5
		Self.f8 = i
		Self.f9 = New TList
		Self.f9.addLast("item")
		Self.f10 = New TMap
		Self.f10.insert("key", "value")
		Self.f11 = New TList
		Self.f12 = New TMap
	End Method
End Type

Type st
	Field f:Int
End Type

Local i1:t1 = New t1

out = JSON.stringify(i1)

check = "{"..
+"~qf1~q:255,"..
+"~qf2~q:65535,"..
+"~qf3~q:2147483647,"..
+"~qf4~q:-9223372036854775808,"..
+"~qf5~q:9.99999968e+37,"..
+"~qf6~q:1.0000000000000000e+308,"..
+"~qf7~q:~qFær Øer~q,"..
+"~qf8~q:{~qf~q:5},"..
+"~qf9~q:[~qitem~q],"..
+"~qf10~q:{~qkey~q:~qvalue~q},"..
+"~qf11~q:[],"..
+"~qf12~q:{}"..
+"}"

If (out = check)
	Print "TEST3 OK"
Else
	Print "TEST3 KO"
	Print out
	Print check
EndIf



''TEST4: object, tab separator

out = JSON.stringify(i1, "~t")

check = "{~n"..
+"~t~qf1~q: 255,~n"..
+"~t~qf2~q: 65535,~n"..
+"~t~qf3~q: 2147483647,~n"..
+"~t~qf4~q: -9223372036854775808,~n"..
+"~t~qf5~q: 9.99999968e+37,~n"..
+"~t~qf6~q: 1.0000000000000000e+308,~n"..
+"~t~qf7~q: ~qFær Øer~q,~n"..
+"~t~qf8~q: {~n"..
+"~t~t~qf~q: 5~n"..
+"~t},~n"..
+"~t~qf9~q: [~n"..
+"~t~t~qitem~q~n"..
+"~t],~n"..
+"~t~qf10~q: {~n"..
+"~t~t~qkey~q: ~qvalue~q~n"..
+"~t},~n"..
+"~t~qf11~q: [],~n"..
+"~t~qf12~q: {}~n"..
+"}"

If (out = check)
	Print "TEST4 OK"
Else
	Print "TEST4 KO"
	Print out
	Print check
EndIf



''TEST 5: object with arrays fields, no separator
Type t2
	Field f1:Byte[2]
	Field f2:Short[2]
	Field f3:Int[2]
	Field f4:Long[2]
	Field f5:Float[2]
	Field f6:Double[2]
	Field f7:String[2]
	Field f8:Object[3]
	Field f9:TList[3]	
	Field f10:TMap[3]
	Field f11:Int[]
	
	Method New()
		Self.f1[0] = 0
		Self.f1[1] = 255
		Self.f2[0] = 0
		Self.f2[1] = 65535
		Self.f3[0] = -2^31
		Self.f3[1] = +2^31-1
		Self.f4[0] = -2^63
		Self.f4[1] = +2^63-1
		Self.f5[0] = 10^-38
		Self.f5[1] = 10^+38
		Self.f6[0] = 10^-308
		Self.f6[1] = 10^+308
		Self.f7[0] = "Fær"
		Self.f7[1] = "Øer"
		Local i1:st = New st
		i1.f = 10
		Self.f8[0] = i1
		Local i2:st = New st
		i2.f = 20
		Self.f8[1] = i2
		Self.f8[2] = Null
		Self.f9[0] = New TList
		Self.f9[0].addLast("An")
		Self.f9[0].addLast("array")
		Self.f9[1] = New TList
		Self.f9[1].addLast("of")
		Self.f9[1].addLast("lists")
		Self.f9[2] = New TList
		Self.f10[0] = New TMap
		Self.f10[0].insert("key0", "value0")
		Self.f10[1] = New TMap
		Self.f10[1].insert("key1", "value1")
		Self.f10[2] = New TMap
	End Method
End Type

Local i2:t2 = New t2

out = JSON.stringify(i2)

check = "{"..
+"~qf1~q:[0,255],"..
+"~qf2~q:[0,65535],"..
+"~qf3~q:[-2147483648,2147483647],"..
+"~qf4~q:[-9223372036854775808,-9223372036854775808],"..
+"~qf5~q:[9.99999935e-39,9.99999968e+37],"..
+"~qf6~q:[9.9999999999999991e-309,1.0000000000000000e+308],"..
+"~qf7~q:[~qFær~q,~qØer~q],"..
+"~qf8~q:[{~qf~q:10},{~qf~q:20},null],"..
+"~qf9~q:[[~qAn~q,~qarray~q],[~qof~q,~qlists~q],[]],"..
+"~qf10~q:[{~qkey0~q:~qvalue0~q},{~qkey1~q:~qvalue1~q},{}],"..
+"~qf11~q:[]"..
+"}"

If (out = check)
	Print "TEST5 OK"
Else
	Print "TEST5 KO"
	Print out
	Print check
EndIf



''TEST6: object with arrays fields, tab separator
out = JSON.stringify(i2, "~t")

check = "{~n"..
+"~t~qf1~q: [~n"..
+"~t~t0,~n"..
+"~t~t255~n"..
+"~t],~n"..
+"~t~qf2~q: [~n"..
+"~t~t0,~n"..
+"~t~t65535~n"..
+"~t],~n"..
+"~t~qf3~q: [~n"..
+"~t~t-2147483648,~n"..
+"~t~t2147483647~n"..
+"~t],~n"..
+"~t~qf4~q: [~n"..
+"~t~t-9223372036854775808,~n"..
+"~t~t-9223372036854775808~n"..
+"~t],~n"..
+"~t~qf5~q: [~n"..
+"~t~t9.99999935e-39,~n"..
+"~t~t9.99999968e+37~n"..
+"~t],~n"..
+"~t~qf6~q: [~n"..
+"~t~t9.9999999999999991e-309,~n"..
+"~t~t1.0000000000000000e+308~n"..
+"~t],~n"..
+"~t~qf7~q: [~n"..
+"~t~t~qFær~q,~n"..
+"~t~t~qØer~q~n"..
+"~t],~n"..
+"~t~qf8~q: [~n"..
+"~t~t{~n"..
+"~t~t~t~qf~q: 10~n"..
+"~t~t},~n"..
+"~t~t{~n"..
+"~t~t~t~qf~q: 20~n"..
+"~t~t},~n"..
+"~t~tnull~n"..
+"~t],~n"..
+"~t~qf9~q: [~n"..
+"~t~t[~n"..
+"~t~t~t~qAn~q,~n"..
+"~t~t~t~qarray~q~n"..
+"~t~t],~n"..
+"~t~t[~n"..
+"~t~t~t~qof~q,~n"..
+"~t~t~t~qlists~q~n"..
+"~t~t],~n"..
+"~t~t[]~n"..
+"~t],~n"..
+"~t~qf10~q: [~n"..
+"~t~t{~n"..
+"~t~t~t~qkey0~q: ~qvalue0~q~n"..
+"~t~t},~n"..
+"~t~t{~n"..
+"~t~t~t~qkey1~q: ~qvalue1~q~n"..
+"~t~t},~n"..
+"~t~t{}~n"..
+"~t],~n"..
+"~t~qf11~q: []~n"..
+"}"

If (out = check)
	Print "TEST6 OK"
Else
	Print "TEST6 KO"
	Print out
	Print check
EndIf



''TEST7: list, no separator
Local li:TList = New TList

Local e1:String = "Fær Øer"
li.addLast(e1)

Local e2:st = New st
e2.f = 9
li.addLast(e2)

Local e3:TList = New TList
li.addLast(e3)

Local e4:TList = New TList
e4.addLast("a")
e4.addLast("list")
li.addLast(e4)

Local e5:TMap = New TMap
li.addLast(e5)

Local e6:TMap = New TMap
e6.insert("key", "value")
li.addLast(e6)

Local e7:Byte[]
li.addLast(e7)

Local e8:Byte[2]
e8[0] = 0
e8[1] = 255
li.addLast(e8)

Local e9:Short[2]
e9[0] = 0
e9[1] = 65535
li.addLast(e9)
	
Local e10:Int[2]
e10[0] = -2^31
e10[1] = +2^31-1
li.addLast(e10)
	
Local e11:Long[2]
e11[0] = -2^63
e11[1] = +2^63-1
li.addLast(e11)
	
Local e12:Float[2]
e12[0] = 10^-38
e12[1] = 10^+38
li.addLast(e12)
	
Local e13:Double[2]
e13[0] = 10^-308
e13[1] = 10^+308
li.addLast(e13)

Local e14:String[2]
e14[0] = "Fær"
e14[1] = "Øer"
li.addLast(e14)

Local e15:st[2]
e15[0] = New st
e15[0].f = 3
e15[1] = New st
e15[1].f = 5
li.addLast(e15)

Local e16:TList[2]
e16[0] = New TList
e16[0].addLast("An")
e16[0].addLast("array")
e16[1] = New TList
e16[1].addLast("of")
e16[1].addLast("lists")
li.addLast(e16)

Local e17:TMap[3]
e17[0] = New TMap
e17[0].insert("key0", "value0")
e17[1] = New TMap
e17[1].insert("key1", "value1")
e17[2] = New TMap
e17[2].insert("key2", Null)
li.addLast(e17)



out = JSON.stringify(li)

check = "[~qFær Øer~q,"..
+"{~qf~q:9},"..
+"[],"..
+"[~qa~q,~qlist~q],"..
+"{},"..
+"{~qkey~q:~qvalue~q},"..
+"[],"..
+"[0,255],[0,65535],"..
+"[-2147483648,2147483647],"..
+"[-9223372036854775808,-9223372036854775808],"..
+"[9.99999935e-39,9.99999968e+37],"..
+"[9.9999999999999991e-309,1.0000000000000000e+308],"..
+"[~qFær~q,~qØer~q],"..
+"[{~qf~q:3},{~qf~q:5}],"..
+"[[~qAn~q,~qarray~q],[~qof~q,~qlists~q]],"..
+"[{~qkey0~q:~qvalue0~q},{~qkey1~q:~qvalue1~q},{~qkey2~q:null}]"..
+"]"

If (out = check)
	Print "TEST7 OK"
Else
	Print "TEST7 KO"
	Print out
	Print check
EndIf



''TEST8: list, tab separator
out = JSON.stringify(li, "~t")

check = "[~n"..
+"~t~qFær Øer~q,~n"..
+"~t{~n"..
+"~t~t~qf~q: 9~n"..
+"~t},~n"..
+"~t[],~n"..
+"~t[~n"..
+"~t~t~qa~q,~n"..
+"~t~t~qlist~q~n"..
+"~t],~n"..
+"~t{},~n"..
+"~t{~n"..
+"~t~t~qkey~q: ~qvalue~q~n"..
+"~t},~n"..
+"~t[],~n"..
+"~t[~n"..
+"~t~t0,~n"..
+"~t~t255~n"..
+"~t],~n"..
+"~t[~n"..
+"~t~t0,~n"..
+"~t~t65535~n"..
+"~t],~n"..
+"~t[~n"..
+"~t~t-2147483648,~n"..
+"~t~t2147483647~n"..
+"~t],~n"..
+"~t[~n"..
+"~t~t-9223372036854775808,~n"..
+"~t~t-9223372036854775808~n"..
+"~t],~n"..
+"~t[~n"..
+"~t~t9.99999935e-39,~n"..
+"~t~t9.99999968e+37~n"..
+"~t],~n"..
+"~t[~n"..
+"~t~t9.9999999999999991e-309,~n"..
+"~t~t1.0000000000000000e+308~n"..
+"~t],~n"..
+"~t[~n"..
+"~t~t~qFær~q,~n"..
+"~t~t~qØer~q~n"..
+"~t],~n"..
+"~t[~n"..
+"~t~t{~n"..
+"~t~t~t~qf~q: 3~n"..
+"~t~t},~n"..
+"~t~t{~n"..
+"~t~t~t~qf~q: 5~n"..
+"~t~t}~n"..
+"~t],~n"..
+"~t[~n"..
+"~t~t[~n"..
+"~t~t~t~qAn~q,~n"..
+"~t~t~t~qarray~q~n"..
+"~t~t],~n"..
+"~t~t[~n"..
+"~t~t~t~qof~q,~n"..
+"~t~t~t~qlists~q~n"..
+"~t~t]~n"..
+"~t],~n"..
+"~t[~n"..
+"~t~t{~n"..
+"~t~t~t~qkey0~q: ~qvalue0~q~n"..
+"~t~t},~n"..
+"~t~t{~n"..
+"~t~t~t~qkey1~q: ~qvalue1~q~n"..
+"~t~t},~n"..
+"~t~t{~n"..
+"~t~t~t~qkey2~q: null~n"..
+"~t~t}~n"..
+"~t]~n"..
+"]"

If (out = check)
	Print "TEST8 OK"
Else
	Print "TEST8 KO"
	Print out
	Print check
EndIf



''TEST9: map, no separator
Local mi:TMap = New TMap

mi.insert("v01", e1)
mi.insert("v02", e2)
mi.insert("v03", e3)
mi.insert("v04", e4)
mi.insert("v05", e5)
mi.insert("v06", e6)
mi.insert("v07", e7)
mi.insert("v08", e8)
mi.insert("v09", e9)
mi.insert("v10", e10)
mi.insert("v11", e11)
mi.insert("v12", e12)
mi.insert("v13", e13)
mi.insert("v14", e14)
mi.insert("v15", e15)
mi.insert("v16", e16)
mi.insert("v17", e17)

out = JSON.stringify(mi)

check = "{"..
+"~qv01~q:~qFær Øer~q,"..
+"~qv02~q:{~qf~q:9},"..
+"~qv03~q:[],"..
+"~qv04~q:[~qa~q,~qlist~q],"..
+"~qv05~q:{},"..
+"~qv06~q:{~qkey~q:~qvalue~q},"..
+"~qv07~q:[],"..
+"~qv08~q:[0,255],"..
+"~qv09~q:[0,65535],"..
+"~qv10~q:[-2147483648,2147483647],"..
+"~qv11~q:[-9223372036854775808,-9223372036854775808],"..
+"~qv12~q:[9.99999935e-39,9.99999968e+37],"..
+"~qv13~q:[9.9999999999999991e-309,1.0000000000000000e+308],"..
+"~qv14~q:[~qFær~q,~qØer~q],"..
+"~qv15~q:[{~qf~q:3},{~qf~q:5}],"..
+"~qv16~q:[[~qAn~q,~qarray~q],[~qof~q,~qlists~q]],"..
+"~qv17~q:[{~qkey0~q:~qvalue0~q},{~qkey1~q:~qvalue1~q},{~qkey2~q:null}]"..
+"}"

If (out = check)
	Print "TEST9 OK"
Else
	Print "TEST9 KO"
	Print out
	Print check
EndIf



''TEST 10: map, no separator
out = JSON.stringify(mi, "~t")

check = "{~n"..
+"~t~qv01~q: ~qFær Øer~q,~n"..
+"~t~qv02~q: {~n"..
+"~t~t~qf~q: 9~n"..
+"~t},~n"..
+"~t~qv03~q: [],~n"..
+"~t~qv04~q: [~n"..
+"~t~t~qa~q,~n"..
+"~t~t~qlist~q~n"..
+"~t],~n"..
+"~t~qv05~q: {},~n"..
+"~t~qv06~q: {~n"..
+"~t~t~qkey~q: ~qvalue~q~n"..
+"~t},~n"..
+"~t~qv07~q: [],~n"..
+"~t~qv08~q: [~n"..
+"~t~t0,~n"..
+"~t~t255~n"..
+"~t],~n"..
+"~t~qv09~q: [~n"..
+"~t~t0,~n"..
+"~t~t65535~n"..
+"~t],~n"..
+"~t~qv10~q: [~n"..
+"~t~t-2147483648,~n"..
+"~t~t2147483647~n"..
+"~t],~n"..
+"~t~qv11~q: [~n"..
+"~t~t-9223372036854775808,~n"..
+"~t~t-9223372036854775808~n"..
+"~t],~n"..
+"~t~qv12~q: [~n"..
+"~t~t9.99999935e-39,~n"..
+"~t~t9.99999968e+37~n"..
+"~t],~n"..
+"~t~qv13~q: [~n"..
+"~t~t9.9999999999999991e-309,~n"..
+"~t~t1.0000000000000000e+308~n"..
+"~t],~n"..
+"~t~qv14~q: [~n"..
+"~t~t~qFær~q,~n"..
+"~t~t~qØer~q~n"..
+"~t],~n"..
+"~t~qv15~q: [~n"..
+"~t~t{~n"..
+"~t~t~t~qf~q: 3~n"..
+"~t~t},~n"..
+"~t~t{~n"..
+"~t~t~t~qf~q: 5~n"..
+"~t~t}~n"..
+"~t],~n"..
+"~t~qv16~q: [~n"..
+"~t~t[~n"..
+"~t~t~t~qAn~q,~n"..
+"~t~t~t~qarray~q~n"..
+"~t~t],~n"..
+"~t~t[~n"..
+"~t~t~t~qof~q,~n"..
+"~t~t~t~qlists~q~n"..
+"~t~t]~n"..
+"~t],~n"..
+"~t~qv17~q: [~n"..
+"~t~t{~n"..
+"~t~t~t~qkey0~q: ~qvalue0~q~n"..
+"~t~t},~n"..
+"~t~t{~n"..
+"~t~t~t~qkey1~q: ~qvalue1~q~n"..
+"~t~t},~n"..
+"~t~t{~n"..
+"~t~t~t~qkey2~q: null~n"..
+"~t~t}~n"..
+"~t]~n"..
+"}"

If (out = check)
	Print "TEST10 OK"
Else
	Print "TEST10 KO"
	Print out
	Print check
EndIf



''TEST11: filter object
Type t3
	Field child_pass_all_contexts:String
	Field child_pass_context2:String
	Field child_pass_never:String
	
	Method New()
		Self.child_pass_all_contexts = "pass_all_contexts"
		Self.child_pass_context2 = "pass_context2"
		Self.child_pass_never = "pass_never"
	End Method
	
	Method json_filter:TList(context:String)
		Local l:TList = New TList
		l.addLast("child_pass_all_contexts")
		If (context = "context2")
			l.addLast("child_pass_context2")
		EndIf
		Return l
	End Method
End Type

Type t4
	Field f3:t3
	Field parent_pass_all_contexts:String
	Field parent_pass_context2:String
	Field parent_pass_never:String
	
	Method New()
		Self.f3 = New t3
		Self.parent_pass_all_contexts = "pass_all_contexts"
		Self.parent_pass_context2 = "pass_context2"
		Self.parent_pass_never = "pass_never"
	End Method
	
	Method json_filter:TList(context:String)
		Local l:TList = New TList
		l.addLast("f3")
		l.addLast("parent_pass_all_contexts")
		If (context = "context2")
			l.addLast("parent_pass_context2")
		EndIf
		Return l
	End Method
End Type

Local i4:t4 = New t4

out = JSON.stringify(i4, "~t")
check = "{~n"..
+"~t~qf3~q: {~n"..
+"~t~t~qchild_pass_all_contexts~q: ~qpass_all_contexts~q~n"..
+"~t},~n"..
+"~t~qparent_pass_all_contexts~q: ~qpass_all_contexts~q~n"..
+"}"

If (out = check)
	Print "TEST11 OK"
Else
	Print "TEST11 KO"
	Print out
	Print check
EndIf



''TEST12: filter object with context
out = JSON.stringify(i4, "~t", "context2")
check = "{~n"..
+"~t~qf3~q: {~n"..
+"~t~t~qchild_pass_all_contexts~q: ~qpass_all_contexts~q,~n"..
+"~t~t~qchild_pass_context2~q: ~qpass_context2~q~n"..
+"~t},~n"..
+"~t~qparent_pass_all_contexts~q: ~qpass_all_contexts~q,~n"..
+"~t~qparent_pass_context2~q: ~qpass_context2~q~n"..
+"}"

If (out = check)
	Print "TEST12 OK"
Else
	Print "TEST12 KO"
	Print out
	Print check
EndIf



''TEST13: filter objects array
Local ia4:t4[2]
ia4[0] = New t4
ia4[1] = New t4
out = JSON.stringify(ia4, "~t")
check = "[~n"..
+"~t{~n"..
+"~t~t~qf3~q: {~n"..
+"~t~t~t~qchild_pass_all_contexts~q: ~qpass_all_contexts~q~n"..
+"~t~t},~n"..
+"~t~t~qparent_pass_all_contexts~q: ~qpass_all_contexts~q~n"..
+"~t},~n"..
+"~t{~n"..
+"~t~t~qf3~q: {~n"..
+"~t~t~t~qchild_pass_all_contexts~q: ~qpass_all_contexts~q~n"..
+"~t~t},~n"..
+"~t~t~qparent_pass_all_contexts~q: ~qpass_all_contexts~q~n"..
+"~t}~n"..
+"]"

If (out = check)
	Print "TEST13 OK"
Else
	Print "TEST13 KO"
	Print out
	Print check
EndIf

''TEST13: filter objects array with context
out = JSON.stringify(ia4, "~t", "context2")
check = "[~n"..
+"~t{~n"..
+"~t~t~qf3~q: {~n"..
+"~t~t~t~qchild_pass_all_contexts~q: ~qpass_all_contexts~q,~n"..
+"~t~t~t~qchild_pass_context2~q: ~qpass_context2~q~n"..
+"~t~t},~n"..
+"~t~t~qparent_pass_all_contexts~q: ~qpass_all_contexts~q,~n"..
+"~t~t~qparent_pass_context2~q: ~qpass_context2~q~n"..
+"~t},~n"..
+"~t{~n"..
+"~t~t~qf3~q: {~n"..
+"~t~t~t~qchild_pass_all_contexts~q: ~qpass_all_contexts~q,~n"..
+"~t~t~t~qchild_pass_context2~q: ~qpass_context2~q~n"..
+"~t~t},~n"..
+"~t~t~qparent_pass_all_contexts~q: ~qpass_all_contexts~q,~n"..
+"~t~t~qparent_pass_context2~q: ~qpass_context2~q~n"..
+"~t}~n"..
+"]"

If (out = check)
	Print "TEST14 OK"
Else
	Print "TEST14 KO"
	Print out
	Print check
EndIf
