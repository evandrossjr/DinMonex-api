trigger:
  branches:
    include:
      - main
      - master

pool:
  vmImage: 'ubuntu-latest'

variables:
  # Nome do JAR e imagem
  IMAGE_NAME: 'dimonex'
  MAVEN_OPTS: '-Dmaven.repo.local=$(Pipeline.Workspace)/.m2/repository'

steps:
# ======================================================
# 1️⃣ Etapa de Build Java + Testes (perfil dev ou test)
# ======================================================
- task: Maven@4
  displayName: 'Compilar e testar com Maven (perfil dev)'
  inputs:
    mavenPomFile: 'pom.xml'
    goals: 'clean package'
    options: '-Pdev -DskipTests=false'
    publishJUnitResults: true
    testResultsFiles: '**/surefire-reports/TEST-*.xml'
    javaHomeOption: 'JDKVersion'
    jdkVersionOption: '1.21'
    mavenOptions: '-Xmx3072m'
    mavenAuthenticateFeed: false

# ======================================================
# 2️⃣ Cache Maven (acelera builds futuros)
# ======================================================
- task: Cache@2
  displayName: 'Cache Maven dependências'
  inputs:
    key: 'maven | "$(Agent.OS)" | **/pom.xml'
    restoreKeys: |
      maven | "$(Agent.OS)"
    path: $(Pipeline.Workspace)/.m2/repository

# ======================================================
# 3️⃣ Build da imagem Docker
# ======================================================
- task: Docker@2
  displayName: 'Build e Push da imagem Docker'
  inputs:
    containerRegistry: '$(dockerRegistryServiceConnection)'  # Conexão configurada no Azure
    repository: '$(IMAGE_NAME)'
    command: 'buildAndPush'
    Dockerfile: 'Dockerfile'
    buildContext: '$(Build.SourcesDirectory)'
    tags: |
      latest
      $(Build.BuildId)

# ======================================================
# 4️⃣ Publicação de Artefato (opcional)
# ======================================================
- task: PublishBuildArtifacts@1
  displayName: 'Publicar .jar como artefato'
  inputs:
    PathtoPublish: 'target/*.jar'
    ArtifactName: 'app'
    publishLocation: 'Container'