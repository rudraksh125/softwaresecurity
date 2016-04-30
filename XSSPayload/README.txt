														PRECISE XSS PAYLOAD GENERATION
	NAMES: KARTHIK VIVEKANANDAN 1207683239
		   ASHISH NAWATHE		1207643780
		   

1. Unzip the <project>.zip file.
2. Import the project in the eclipse the project has all the dependent jar files included.
3. Run the code with the agruments. java Launcher <test-input> <url/path for HTML file>
4. The code will print the possible payload for the corresponding HTML file.
5. Copy the payload and insert it in the HTML file and check the payload executed.
		For multiple vulnerabilities multiple payloads can be outputed.
7. Please copy the output and then paste it in the place of corresponding input in HTML and check the output.
6. Test with different HTML files and contexts.

NOTES:
1. In cases where payload is generated as an HTML attribute onclick -pop ups will be generated after the click event on the text.
2. In cases where the page has multiple inputs, please run the program with one input at a time and check if it is vulnerable.
3. In cases where Payload is generated with \n please include \n while putting the payload.
