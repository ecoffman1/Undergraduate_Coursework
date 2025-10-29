// @ts-ignore
import * as http from "http";
// @ts-ignore
import * as fs from "fs";
// @ts-ignore
import map from './map.json';

const host: string = '172.24.192.1';
const port: number = 8080;

interface GameObject {
    name: string;
    x: number;
    y: number;
    score: number;
}

interface Fireball {
    x: number;
    y: number;
    who: number;
}

interface RequestPayload {
    id: string;
    name?: string;
    x?: number;
    y?: number;
    mapitem?: boolean;
}

interface ResponsePayload {
    players: { [id: string]: GameObject };
    fireballs: { [id: string]: Fireball };
}

let objects: { [id: string]: GameObject } = {};
let fireballs: { [id: string]: Fireball } = {};
let items = map.items;

const someMimeTypes: { [ext: string]: string } = {
    '.html': 'text/html',
    '.ico': 'image/png'
    '.jpeg': 'image/jpeg',
    '.jpg': 'image/jpeg',
    '.js': 'text/javascript',
    '.png': 'image/png',
    '.svg': 'image/svg+xml',
    '.zip': 'application/zip',
};

const requestListener = (request: http.IncomingMessage, response: http.ServerResponse): void => {
    let body = '';
    request.on('data', (chunk: Buffer) => {
        body += chunk;
    });

    request.on('end', () => {
        console.log(`Got a request for ${request.url}, body=${body}`);
        let filename = request.url ? request.url.substring(1) : ''; // cut off the '/'
        if (filename.length === 0) filename = 'client.html';

        const lastDot = filename.lastIndexOf('.');
        const extension = lastDot >= 0 ? filename.substring(lastDot) : '';

        if (filename === 'generated.html') {
            response.setHeader("Content-Type", "text/html");
            response.writeHead(200);
            response.end(`<html><body><h1>Random number: ${Math.random()}</h1></body></html>`);
        } else if (extension in someMimeTypes && fs.existsSync(filename)) {
            fs.readFile(filename, (err, data) => {
                if (err) {
                    response.writeHead(500);
                    response.end('Error reading file');
                } else {
                    response.setHeader("Content-Type", someMimeTypes[extension]);
                    response.writeHead(200);
                    response.end(data);
                }
            });
        } else if (filename === 'update') {
            const payload: ResponsePayload = { players: objects, fireballs: fireballs };
            response.setHeader('Content-Type', 'application/json');
            response.writeHead(200);
            response.end(JSON.stringify(payload));
        } else if (filename === 'connect') {
            const payload: RequestPayload = JSON.parse(body);
            objects[payload.id] = { name: payload.name || '', x: 500, y: 250, score: 0 };
            console.log(JSON.stringify(objects));
            response.setHeader('Content-Type', 'application/json');
            response.writeHead(200);
            response.end(JSON.stringify(items));
        } else if (filename === 'leftClick') {
            const payload: RequestPayload = JSON.parse(body);
            if (objects[payload.id]) {
                objects[payload.id].x = payload.x || 0;
                objects[payload.id].y = payload.y || 0;
            }
            response.setHeader('Content-Type', 'application/json');
            response.writeHead(200);
            response.end("Clicked");
        } else if (filename === 'collision') {
            const payload: RequestPayload = JSON.parse(body);
            console.log(payload.mapitem);
            if (payload.mapitem) {
                if (objects[payload.id]) objects[payload.id].score = 0;
            } else {
                if (objects[payload.id]) objects[payload.id].score += 1;
            }
            response.setHeader('Content-Type', 'application/json');
            response.writeHead(200);
            response.end("Score Updated");
        } else if (filename === 'rightClick') {
            const payload: RequestPayload = JSON.parse(body);
            fireballs[payload.id] = { x: payload.x || 0, y: payload.y || 0, who: Math.floor(Math.random() * 100000) };
            response.setHeader('Content-Type', 'application/json');
            response.writeHead(200);
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
