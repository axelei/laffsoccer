SuperStrict

Import "t_game_mode.bmx"

Type t_menu_copying Extends t_game_mode

	Field copying:String[198]
	Field lines:Int
	Field position:Int

	Method New()
		lines = 198
		RestoreData l_copying
		For Local line:Int = 0 To Self.lines -1
			ReadData Self.copying[line]
			Self.copying[line] = Replace(Self.copying[line],"%",Chr(34))
		Next	
	End Method

	Method update()
		If KeyDown(KEY_DOWN) 
			Self.position = Min(Self.position +1, Self.lines -1 -30)
		Else If KeyDown(KEY_UP)
			Self.position = Max(0, Self.position -1)
		Else If KeyDown(KEY_ESCAPE)
			wait_nokey()
			Repeat Until Not GetChar()
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_INTRO)
		EndIf
	End Method
	
	Method render()
		Cls
		SetColor(light, light, light)
		DrawLine 512 + 355, 132, 512 + 355, 600
		DrawLine 512 - 355, 132, 512 - 355, 600
		DrawLine 512 - 355, 132, 512 + 355, 132
		DrawLine 512 - 355, 600, 512 + 355, 600
		DrawRect 512 + 355 -2, 136 + 450 * Self.position / (Self.lines -30), 5, 10
		If Self.position > 0
			DrawText "...", 180, 156
		EndIf
		For Local line:Int = 0 To Self.lines -1
			If line => Self.position And line <= Self.position +30
				DrawText Self.copying[line], 180, 180 +12*(line -Self.position)
			EndIf
		Next
		If Self.position < (Self.lines -1 -30)
			DrawText "...", 180, 564
		EndIf
		DrawText "Press arrow keys to scroll text, 'ESC' to return", 512, 648
	End Method
		
End Type

#l_copying
DefData "                      GNU GENERAL PUBLIC LICENSE"
DefData "   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION"
DefData ""
DefData "  0. This License applies to any program or other work which contains"
DefData "a notice placed by the copyright holder saying it may be distributed"
DefData "under the terms of this General Public License.  The %Program%, below,"
DefData "refers to any such program or work, and a %work based on the Program%"
DefData "means either the Program or any derivative work under copyright law:"
DefData "that is to say, a work containing the Program or a portion of it,"
DefData "either verbatim or with modifications and/or translated into another"
DefData "language.  (Hereinafter, translation is included without limitation in"
DefData "the term %modification%.)  Each licensee is addressed as %you%."
DefData ""
DefData "Activities other than copying, distribution and modification are not"
DefData "covered by this License; they are outside its scope.  The act of"
DefData "running the Program is not restricted, and the output from the Program"
DefData "is covered only if its contents constitute a work based on the"
DefData "Program (independent of having been made by running the Program)."
DefData "Whether that is true depends on what the Program does."
DefData ""
DefData "  1. You may copy and distribute verbatim copies of the Program's"
DefData "source code as you receive it, in any medium, provided that you"
DefData "conspicuously and appropriately publish on each copy an appropriate"
DefData "copyright notice and disclaimer of warranty; keep intact all the"
DefData "notices that refer to this License and to the absence of any warranty;"
DefData "and give any other recipients of the Program a copy of this License"
DefData "along with the Program."
DefData ""
DefData "You may charge a fee for the physical act of transferring a copy, and"
DefData "you may at your option offer warranty protection in exchange for a fee."
DefData ""
DefData "  2. You may modify your copy or copies of the Program or any portion"
DefData "of it, thus forming a work based on the Program, and copy and"
DefData "distribute such modifications or work under the terms of Section 1"
DefData "above, provided that you also meet all of these conditions:"
DefData ""
DefData "    a) You must cause the modified files to carry prominent notices"
DefData "    stating that you changed the files and the date of any change."
DefData ""
DefData "    b) You must cause any work that you distribute or publish, that in"
DefData "    whole or in part contains or is derived from the Program or any"
DefData "    part thereof, to be licensed as a whole at no charge to all third"
DefData "    parties under the terms of this License."
DefData ""
DefData "    c) If the modified program normally reads commands interactively"
DefData "    when run, you must cause it, when started running for such"
DefData "    interactive use in the most ordinary way, to print or display an"
DefData "    announcement including an appropriate copyright notice and a"
DefData "    notice that there is no warranty (or else, saying that you provide"
DefData "    a warranty) and that users may redistribute the program under"
DefData "    these conditions, and telling the user how to view a copy of this"
DefData "    License.  (Exception: if the Program itself is interactive but"
DefData "    does not normally print such an announcement, your work based on"
DefData "    the Program is not required to print an announcement.)"
DefData ""
DefData "These requirements apply to the modified work as a whole.  If"
DefData "identifiable sections of that work are not derived from the Program,"
DefData "and can be reasonably considered independent and separate works in"
DefData "themselves, then this License, and its terms, do not apply to those"
DefData "sections when you distribute them as separate works.  But when you"
DefData "distribute the same sections as part of a whole which is a work based"
DefData "on the Program, the distribution of the whole must be on the terms of"
DefData "this License, whose permissions for other licensees extend to the"
DefData "entire whole, and thus to each and every part regardless of who wrote it."
DefData ""
DefData "Thus, it is not the intent of this section to claim rights or contest"
DefData "your rights to work written entirely by you; rather, the intent is to"
DefData "exercise the right to control the distribution of derivative or"
DefData "collective works based on the Program."
DefData ""
DefData "In addition, mere aggregation of another work not based on the Program"
DefData "with the Program (or with a work based on the Program) on a volume of"
DefData "a storage or distribution medium does not bring the other work under"
DefData "the scope of this License."
DefData ""
DefData "  3. You may copy and distribute the Program (or a work based on it,"
DefData "under Section 2) in object code or executable form under the terms of"
DefData "Sections 1 and 2 above provided that you also do one of the following:"
DefData ""
DefData "    a) Accompany it with the complete corresponding machine-readable"
DefData "    source code, which must be distributed under the terms of Sections"
DefData "    1 and 2 above on a medium customarily used for software interchange; or,"
DefData ""
DefData "    b) Accompany it with a written offer, valid for at least three"
DefData "    years, to give any third party, for a charge no more than your"
DefData "    cost of physically performing source distribution, a complete"
DefData "    machine-readable copy of the corresponding source code, to be"
DefData "    distributed under the terms of Sections 1 and 2 above on a medium"
DefData "    customarily used for software interchange; or,"
DefData ""
DefData "    c) Accompany it with the information you received as to the offer"
DefData "    to distribute corresponding source code.  (This alternative is"
DefData "    allowed only for noncommercial distribution and only if you"
DefData "    received the program in object code or executable form with such"
DefData "    an offer, in accord with Subsection b above.)"
DefData ""
DefData "The source code for a work means the preferred form of the work for"
DefData "making modifications to it.  For an executable work, complete source"
DefData "code means all the source code for all modules it contains, plus any"
DefData "associated interface definition files, plus the scripts used to"
DefData "control compilation and installation of the executable.  However, as a"
DefData "special exception, the source code distributed need not include"
DefData "anything that is normally distributed (in either source or binary"
DefData "form) with the major components (compiler, kernel, and so on) of the"
DefData "operating system on which the executable runs, unless that component"
DefData "itself accompanies the executable."
DefData ""
DefData "If distribution of executable or object code is made by offering"
DefData "access to copy from a designated place, then offering equivalent"
DefData "access to copy the source code from the same place counts as"
DefData "distribution of the source code, even though third parties are not"
DefData "compelled to copy the source along with the object code."
DefData ""
DefData "  4. You may not copy, modify, sublicense, or distribute the Program"
DefData "except as expressly provided under this License.  Any attempt"
DefData "otherwise to copy, modify, sublicense or distribute the Program is"
DefData "void, and will automatically terminate your rights under this License."
DefData "However, parties who have received copies, or rights, from you under"
DefData "this License will not have their licenses terminated so long as such"
DefData "parties remain in full compliance."
DefData ""
DefData "  5. You are not required to accept this License, since you have not"
DefData "signed it.  However, nothing else grants you permission to modify or"
DefData "distribute the Program or its derivative works.  These actions are"
DefData "prohibited by law if you do not accept this License.  Therefore, by"
DefData "modifying or distributing the Program (or any work based on the"
DefData "Program), you indicate your acceptance of this License to do so, and"
DefData "all its terms and conditions for copying, distributing or modifying"
DefData "the Program or works based on it."
DefData ""
DefData "  6. Each time you redistribute the Program (or any work based on the"
DefData "Program), the recipient automatically receives a license from the"
DefData "original licensor to copy, distribute or modify the Program subject to"
DefData "these terms and conditions.  You may not impose any further"
DefData "restrictions on the recipients' exercise of the rights granted herein."
DefData "You are not responsible for enforcing compliance by third parties to"
DefData "this License."
DefData ""
DefData "  7. If, as a consequence of a court judgment or allegation of patent"
DefData "infringement or for any other reason (not limited to patent issues),"
DefData "conditions are imposed on you (whether by court order, agreement or"
DefData "otherwise) that contradict the conditions of this License, they do not"
DefData "excuse you from the conditions of this License.  If you cannot"
DefData "distribute so as to satisfy simultaneously your obligations under this"
DefData "License and any other pertinent obligations, then as a consequence you"
DefData "may not distribute the Program at all.  For example, if a patent"
DefData "license would not permit royalty-free redistribution of the Program by"
DefData "all those who receive copies directly or indirectly through you, then"
DefData "the only way you could satisfy both it and this License would be to"
DefData "refrain entirely from distribution of the Program."
DefData ""
DefData "If any portion of this section is held invalid or unenforceable under"
DefData "any particular circumstance, the balance of the section is intended to"
DefData "apply and the section as a whole is intended to apply in other"
DefData "circumstances."
DefData ""
DefData "It is not the purpose of this section to induce you to infringe any"
DefData "patents or other property right claims or to contest validity of any"
DefData "such claims; this section has the sole purpose of protecting the"
DefData "integrity of the free software distribution system, which is"
DefData "implemented by public license practices.  Many people have made"
DefData "generous contributions to the wide range of software distributed"
DefData "through that system in reliance on consistent application of that"
DefData "system; it is up to the author/donor to decide if he or she is willing"
DefData "to distribute software through any other system and a licensee cannot"
DefData "impose that choice."
DefData ""
DefData "This section is intended to make thoroughly clear what is believed to"
DefData "be a consequence of the rest of this License."
DefData ""
DefData "  8. If the distribution and/or use of the Program is restricted in"
DefData "certain countries either by patents or by copyrighted interfaces, the"
DefData "original copyright holder who places the Program under this License"
DefData "may add an explicit geographical distribution limitation excluding"
DefData "those countries, so that distribution is permitted only in or among"
DefData "countries not thus excluded.  In such case, this License incorporates"
DefData "the limitation as if written in the body of this License."
DefData ""
DefData "  9. The Free Software Foundation may publish revised and/or new versions"
DefData "of the General Public License from time to time.  Such new versions will"
DefData "be similar in spirit to the present version, but may differ in detail to"
DefData "address new problems or concerns."
DefData ""
DefData "Each version is given a distinguishing version number.  If the Program"
DefData "specifies a version number of this License which applies to it and %any"
DefData "later version%, you have the option of following the terms and conditions"
DefData "either of that version or of any later version published by the Free"
DefData "Software Foundation.  If the Program does not specify a version number of"
DefData "this License, you may choose any version ever published by the Free Software"
DefData "Foundation."
DefData ""
DefData "  10. If you wish to incorporate parts of the Program into other free"
DefData "programs whose distribution conditions are different, write to the author"
DefData "to ask for permission.  For software which is copyrighted by the Free"
DefData "Software Foundation, write to the Free Software Foundation; we sometimes"
DefData "make exceptions for this.  Our decision will be guided by the two goals"
DefData "of preserving the free status of all derivatives of our free software and"
DefData "of promoting the sharing and reuse of software generally."
