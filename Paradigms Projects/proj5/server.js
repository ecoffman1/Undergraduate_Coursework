const http = require("http");
const fs = require("fs");

const host = '172.24.192.1';
const port = 8080;
let objects = [];
let fireballs = [];

const some_mime_types = {
    '.html': 'text/html',
    '.ico': 'image/png',
    '.jpeg': 'image/jpeg',
    '.jpg': 'image/jpeg',
    '.js': 'text/javascript',
    '.png': 'image/png',
    '.svg': 'image/svg+xml',
    '.zip': 'application/zip',
}

const requestListener = (request, response) => {
    let body = '';
    request.on('data', (chunk) => {
      body += chunk;
    });
    request.on('end', () => {
      console.log(`Got a request for ${request.url}, body=${body}`);
      let filename = request.url.substring(1); // cut off the '/'
      if (filename.length === 0)
        filename = 'client.html';
      const last_dot = filename.lastIndexOf('.');
      const extension = last_dot >= 0 ? filename.substring(last_dot) : '';
      if (filename === 'generated.html') {
          response.setHeader("Content-Type", "text/html");
          response.writeHead(200);
          response.end(`<html><body><h1>Random number: ${Math.random()}</h1></body></html>`);

      } else if (extension in some_mime_types && fs.existsSync(filename)) {
          fs.readFile(filename, null, (err, data) => {
              response.setHeader("Content-Type", some_mime_types[extension]);
              response.writeHead(200);
              response.end(data);
          });

      } else if(filename === 'update'){
        response.setHeader('Content-Type', 'application/json');
        response.writeHead(200);
        let payload = JSON.parse(body);
        response.end(JSON.stringify({players:objects,fireballs:fireballs}));

      } else if(filename === 'connect'){
        response.setHeader('Content-Type', 'application/json');
        response.writeHead(200);
        objects.push({id:JSON.parse(body).id ,x:500,y:250});
        console.log(JSON.stringify(objects));
        response.end("Welcome");

      } else if(filename === 'leftClick'){
        response.setHeader('Content-Type', 'application/json');
        response.writeHead(200);
        let request_ob = JSON.parse(body);
        for(let i = 0; i < objects.length;i++){
          if(request_ob.id === objects[i].id){
            objects[i].x = request_ob.x;
            objects[i].y = request_ob.y;
          }
        }
        response.end("Clicked")

      } else if(filename === 'rightClick'){
        response.setHeader('Content-Type', 'application/json');
        response.writeHead(200);
        let payload = JSON.parse(body);
        fireballs.push({id:payload.id,x:payload.x,y:payload.y,who: Math.floor(Math.random() * 100000)});
        response.end("Clicked");

      } else {
          response.setHeader("Content-Type", "text/html");
          response.writeHead(404);
          response.end(`<html><body><h1>404 - Not found</h1><p>There is no file named "${filename}".</body></html>`);
      }
    });
};

const server = http.createServer(requestListener);
server.listen(port, host, () => {
    console.log(`Server is running on http://${host}:${port}`);
});