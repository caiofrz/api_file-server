[JAVA_BADGE]:https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white

[SPRING_BADGE]: https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white


<h1 align="center" style="font-weight: bold;">API File-Server</h1>

<div align="center">  

![java][JAVA_BADGE]
![spring][SPRING_BADGE]

</div>

<p align="center">
 <a href="#started">Getting Started</a> • 
  <a href="#routes">API Endpoints</a> •
</p>

<p align="center">
  <b>API para upload/download de arquivos e conversão de imagem e documento de texto para pdf.</b>
</p>

<h2 id="started">🚀 Getting started</h2>

Clone o projeto:
```bash
git clone https://github.com/caiofrz/api_file-server.git
cd api_file-server
``````

<h3>Pre requisitos</h3>

- [Java 17+]([https://github.com/](https://www.java.com/pt-BR/download/manual.jsp))


<h3>Rodando o projeto</h3>

```bash
mvn spring-boot:run
``````

<h2 id="routes">📍 API Endpoints</h2>

Here you can list the main routes of your API, and what are their expected request bodies.
​
| route | description                                          
|----------------------|-----------------------------------------------------
| <kbd>POST /api/files/upload</kbd>     | Realiza o upload de arquivo
| <kbd>GET /api/files/download/{arquivo}</kbd>     | recupera o arquivo
| <kbd>GET /api/files/list</kbd>     | recupera uma lista com nome de todos os arquivos salvos no servidor<br />
| <kbd>POST /api/files/convert-to-pdf</kbd>     | realiza a conversão do arquivo em pdf. (Aquivos suportados: .png, .jpg .jpeg, .docx, .doc)

<h3 id="upload">POST /api/files/upload</h3>

**REQUEST**
Lembre de que esta é um requisição form-data
```json
{
  "file": "back_log_23_02_2023.docx"
}
```

**RESPONSE**

```json
{
    "status": "OK",
    "message": "Upload bem sucessido!",
    "fileDownloadUri": "http://localhost:8080/api/files/download/back_log_23_02_2023.docx"
}
```
<h3 id="upload">POST /api/files/convert-to-pdf</h3>

**REQUEST**
Lembre de que esta é um requisição form-data
```json
{
  "file": "back_log_23_02_2023.docx"
}
```

**RESPONSE**

```json
{
    "status": "OK",
    "message": "Conversão bem sucessida!",
    "fileDownloadUri": "http://localhost:8080/api/files/download/back_log_23_02_2023.pdf"
}
```

<h3 id="get-list-files">GET /api/files/list </h3>

**RESPONSE**

```json
{
    "location": "C:/Users/caiof/OneDrive/Documentos/Projetos/api.file_server/src/main/resources/uploads",
    "size": 7,
    "files": [
        "back_log_23_02_2023.docx",
        "grafico-perfil.pdf",
        "img perfil 2.pdf",
        "img perfil.pdf",
        "launch_options_csgo.txt",
        "logoifnmg.pdf",
        "Reservar_Quarto.pdf"
    ]
}
```

## Funcionalidades

- Upload de arquivos
- Download de arquivos
- Consultas de arquivos salvos
- Conversão de arquivos para pdf


## Feedback

Se você tiver algum feedback, por favor não deixe de dá-lo.

Me contate através do [github](https://github.com/caiofrz)
ou [LinkedIn](https://www.linkedin.com/in/caio-ferraz-almeida/) 
