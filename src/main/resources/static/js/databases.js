let themes;

window.onload = function () {
  fetch('/characters.json')
    .then(response => response.json())
    .then(data => {
      themes = data.themes;
      populateTestDataThemes();
      updateCharacters();
    });
};

function populateTestDataThemes() {
  const themeSelect = document.getElementById('theme');
  for (const key in themes) {
    if (themes.hasOwnProperty(key)) {
      const option = document.createElement('option');
      option.value = key;
      option.textContent = themes[key].name;
      themeSelect.appendChild(option);
    }
  }
}

function updateCharacters() {
  const theme = document.getElementById('theme').value;
  const charactersList = document.getElementById('charactersList');
  charactersList.innerHTML = '';

  if (themes[theme]) {
    const characters = themes[theme].characters;
    for (const character of characters) {
      const li = document.createElement('li');
      li.textContent = character;
      charactersList.appendChild(li);
    }
  }
}

function confirmDrop() {
  // handle empty - no database selected
  const dbSelect = document.getElementById("dropDatabase");
  if (dbSelect.value.trim() === "") {
    alert("Please select a database before dropping.");
    return false;
  }
  return confirm("Are you sure you want to drop this database?")
}

function resetDbMigrationsList() {
  const selectElement = document.getElementById('migrations');
  selectElement.innerHTML = '';
  return selectElement;
}

function init() {
  //
  const btnDownloadBlueprintSql = document.getElementById('btn-download-blueprint-sql');
  btnDownloadBlueprintSql.addEventListener('click', () => {
    // alert('download blueprint sql');
    window.open('/download?fileName=blueprint.sql', '_blank');
  });

  const btnDownloadTestUsersSql = document.getElementById('btn-download-test-users-sql');
  btnDownloadTestUsersSql.addEventListener('click', () => {
    alert('download test-users sql');
  });

  // dropdown -> database select
  const databaseSelect = document.getElementById('database');
  databaseSelect.addEventListener('change', (event) => {
    const selectedDatabase = event.target.value;
    console.log(`selected database is [${selectedDatabase}]`);
    resetDbMigrationsList();
  });

  // button -> list database migrations
  const btnListMigrationsInSelectedDatabase = document.getElementById('btnListMigrations');
  btnListMigrationsInSelectedDatabase.addEventListener('click', () => {
    const selectedDatabase = databaseSelect.value;
    if (!selectedDatabase) {
      alert('Unable to continue, NO Database Selected');
      databaseSelect.focus();
      return;
    }

    const txtSchemaTableName = document.getElementById('schemaTableName');
    const schemaTable = txtSchemaTableName.value;

    const url = `/flyway-schemas?schematable=${schemaTable}&database=${encodeURIComponent(selectedDatabase)}`;
    console.log(`getting list of migrations from [${selectedDatabase}] database...`);
    fetch(url)
      .then(res => res.json())
      .then(arr => {
        console.log(arr);
        arr.reverse();

        const selectElement = resetDbMigrationsList();

        arr.forEach(csv => {
          const item = csv.split(',').join(' | ');
          const newOption = document.createElement('option');
          newOption.value = csv;
          newOption.textContent = item;
          selectElement.appendChild(newOption);
        });
      });
  });
}

document.addEventListener('DOMContentLoaded', () => {
  init();
});