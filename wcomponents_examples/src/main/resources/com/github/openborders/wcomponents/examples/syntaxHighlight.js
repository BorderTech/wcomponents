(function(){
	try
	{
		var highlightedSource = [],
			// Keywords from http://java.sun.com/docs/books/tutorial/java/nutsandbolts/_keywords.html
			keywords = {
				"abstract":true,"continue":true,"for":true,"new":true,"switch":true,
				"assert":true,"default":true,"goto":true,"package":true,"synchronized":true,
				"boolean":true,"do":true,"if":true,"private":true,"this":true,
				"break":true,"double":true,"implements":true,"protected":true,"throw":true,
				"byte":true,"else":true,"import":true,"public":true,"throws":true,
				"case":true,"enum":true,"instanceof":true,"return":true,"transient":true,
				"catch":true,"extends":true,"int":true,"short":true,"try":true,
				"char":true,"final":true,"interface":true,"static":true,"void":true,
				"class":true,"finally":true,"long":true,"strictfp":true,"volatile":true,
				"const":true,"float":true,"native":true,"super":true,"while":true
			},
			appendKeyword,
			appendComment,
			appendJavadocComment,
			appendTextString,
			appendText;
		// Java source highlighting
		function doHighlighting(sourceContainer)
		{	
			var container = document.getElementById(sourceContainer),
				i, lines, 
				inComment = false, inJavadoc = false, // These are the only things that can span multiple lines.
				inString, text, textPos, lastChar, j, textLen, lineCount;
				
			if(container)
			{
				container = container.firstChild;
				
				lines = container.innerHTML.replace(/<br[^>]*>/ig, "\n").replace(/&nbsp;/g, ' ').split("\n");

				for (i=0, lineCount=lines.length; i<lineCount ; i++)
				{
					inString = false;
					
					text = lines[i];
					textPos = 0;
					lastChar=' ';
					
					for (j=0,textLen=text.length; j<textLen ; j++)
					{
						switch (text.charAt(j))
						{
							case '"':
							{
								if (!inComment && !inJavadoc)
								{
									if (inString == false)
									{
										inString = true;
									}
									else if (inString && lastChar != '\'')
									{
										inString = false;
									}
								}
								
								break;
							}
							case '*':
							{
								if (lastChar=='/' && !inComment && !inJavadoc && !inString)
								{
									parseRow(text.substring(textPos, j-1));
									textPos = j-1;
									
									// Start of a multi-line comment
									// peek at next char for javadoc comment
									if (j+1 < text.length && text.charAt(j+1) == '*')
									{
										inJavadoc = true;
									}
									else
									{
										inComment = true;
									}
								}
								
								break;
							}
							case '/':
							{
								if (lastChar=='*' && !inString && (inComment || inJavadoc))
								{
									if (inJavadoc)
									{
										inJavadoc = false;
										appendJavadocComment(text.substring(textPos, j+1));
									}
									else // inComment
									{
										inComment = false;
										appendComment(text.substring(textPos, j+1));
									}
									
									textPos = j+1;
								}
								else if (lastChar=='/' && !inComment && !inJavadoc && !inString)
								{
									// Rest of the line is a comment
									parseRow(text.substring(textPos, j-1));
									
									if (inJavadoc)
									{
										appendJavadocComment(text.substring(j-1));
									}
									else
									{
										appendComment(text.substring(j-1));
									}
									
									j = text.length;
									textPos = j;
								}
								
								break;
							}
						}
						
						lastChar = text.charAt(j);
					}
			
					// At EOL
					if (inJavadoc)
					{
						appendJavadocComment(text.substring(textPos));
					}
					else if (inComment)
					{
						appendComment(text.substring(textPos));
					}
					else
					{
						parseRow(text.substring(textPos));
					}
					if(text)
					{
						appendRaw('<br/>');
					}
				}
				
				container.innerHTML = highlightedSource.join('');
			}
		}
		
		function parseRow(text)
		{
			var inString = false,
				textPos = 0,
				lastChar=' ',
				lastToken='', j, textLen, nextChar;
		
			for (j=0, textLen=text.length; j<textLen; j++)
			{
				nextChar = text.charAt(j);
				if (inString)
				{
					if (nextChar == '"' && lastChar!='\\')
					{
						appendTextString(text.substring(textPos, j+1));
						inString = false;
						textPos = j+1;
					}
				}
				else
				{
					switch (nextChar)
					{
						case '"':
						{					
							appendText(text.substring(textPos, j));
							inString = true;
							textPos = j;
							break;
						}				
						case '}':
						case '{':
						case ')':
						case '(':
						case ';':
						case ':':
						case ' ':
						case '\t':
						{
							if (isJavaKeyword(lastToken))				
							{
								appendText(text.substring(textPos, j-lastToken.length));
								appendKeyword(lastToken);
								textPos = j;
							}
			
							lastToken = '';
							
							break;
						}
						default:
							lastToken += nextChar;
					}
				}		
				
				lastChar = nextChar;
			}	
			
			lastToken = text.substring(textPos); 
			
			if (isJavaKeyword(lastToken.trim()))	
			{
				appendKeyword(lastToken);
			}
			else
			{
				appendText(lastToken);
			}
		}
		
		function append(clazz, text)
		{
			if(text)
			{
				highlightedSource[highlightedSource.length] = "<span class='";
				highlightedSource[highlightedSource.length] = clazz;
				highlightedSource[highlightedSource.length] = "'>";
				highlightedSource[highlightedSource.length] = text;//.replace(/ /g, '&nbsp;');
				highlightedSource[highlightedSource.length] = "</span>";
			}
		}
		
		function appendRaw(text)
		{
			highlightedSource[highlightedSource.length] = text;
		}
		
		//
		// Is the given token a java keyword?
		//
		// token: The token to check
		// return: true if the supplied token is a java keyword, false otherwise
		//
		function isJavaKeyword(token)
		{		
			return (token in keywords) && keywords.hasOwnProperty(token);
		}
		
		appendKeyword = append.bind(this, "javaSourceKeyword");
		appendComment = append.bind(this, "javaSourceComment");
		appendJavadocComment = append.bind(this, "javaSourceJavadoc");
		appendTextString = append.bind(this, "javaSourceString");
		appendText = append.bind(this, "javaSourceText");
		
		window.doHighlighting = doHighlighting;
	}
	catch(e)
	{
		//balls-up
	}
})();