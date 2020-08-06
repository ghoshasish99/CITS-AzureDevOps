/*
 * Copyright 2014 - 2017 Cognizant Technology Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @returns {UIClient}
 */
function HarClient() {

    //<editor-fold defaultstate="collapsed" desc="Meta">
    this.version = "0.0.1";
    //</editor-fold>
    var conn;
    var callback = {},
            onOpen, onError, onReportHistory, onHarsData, onSaveRefRes, onHarRefData;
    //<editor-fold defaultstate="collapsed" desc="Api Response Handler">


    onOpen = function (e) {
        this.connected = true;
        console.log('connected');
    };
    var onMessage = function (event) {
        var msg = JSON.parse(event.data);
        switch (msg.res) {
            case "SET.HarsData":
            case "SET.ReportHar":
            case "SET.ReportHistory":
            case "SET.RefData":
            case "RES.SaveHarRef":
            case "DELETE.Har":
                callback[msg.DATA.callback](msg.DATA);
                break;
            case 'Err':
                this.onClose = $.OnServerClose;
                onError(msg);
                break;
            case 'Comm':
                $.onServerMsg(msg);
                break;
            default:
                this.onClose = $.OnServerClose;
                onError("Unknown protocol");
        }
    };
    //</editor-fold>
    this.connected = false;
    //<editor-fold defaultstate="collapsed" desc="Api Request Handler">
    this.getHarRefData = function (op) {
        op.req = "GET.RefData";
        this.send(op);
    };
    this.getHarsData = function (op) {
        op.req = 'GET.HarsData';
        this.send(op);
    };
    this.getReportHistory = function (op) {
        op.req = 'GET.ReportHistory';
        this.send(op);
    };
    this.clearSelectedHars = function (op) {
        op = op || {};
        op.req = 'CLR.SelectedHars';
        this.send(op);
    };
    this.saveRefHar = function (op) {
        op.req = 'SAVE.HarRef';
        this.send(op);
    };
    this.harDelete = function (op) {
        op.req = 'DELETE.Har';
        this.send(op);
    };
    this.send = function (data) {
        if (data.callback) {
            callback[data.callback.name] = data.callback;
            data.callback = data.callback.name;
        }
        conn.send(typeof data === 'string' ? data : JSON.stringify(data));
    };
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Socket Connection">
    this.connect = function (options) {
        var socketURI = window.location.protocol.replace("http", "ws") + "//" + window.location.host + $.WEBUI_API_PATH;
        console.log(socketURI);
        conn = new WebSocket(socketURI);
        conn.onopen = function (e) {
            onOpen(e);
            if (options.onOpen)
                options.onOpen(e);
        };
        conn.onmessage = onMessage;
        conn.error = options.onErr;
        this.onClose = $.OnServerClose;
        conn.onclose = this.onClose;
        onError = options.onServerErr;
    };
    //</editor-fold>
}
var harClient = new HarClient();